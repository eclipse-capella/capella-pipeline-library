# Capella Pipeline Libray
The official Capella Pipeline Library repository

## Dependencies
Maintaining minimal dependencies to OS specific binaries is essential. Here is the current list of dependencies:
* java
* curl
* ssh
* scp
* sleep

## macOS notarization

The shared `macOsNotarizer(productsDirectory, dmgPatterns)` step submits signed DMGs
to Eclipse CBI for notarization and stapling, then verifies the downloaded images
and their MD5, SHA-1 and SHA-512 sidecars. It runs on the Linux build agent; it does
not perform the preceding macOS code-signing or DMG packaging.

```groovy
stage('Notarize macOS products') {
    when { expression { !github.isPullRequest() } }
    options { timeout(time: 90, unit: 'MINUTES') }
    steps {
        macOsNotarizer(
            "${WORKSPACE}/releng/plugins/org.polarsys.capella.rcp.product/target/products",
            [
                'capella-*-macosx-cocoa-x86_64.dmg',
                'capella-*-macosx-cocoa-aarch64.dmg'
            ]
        )
    }
}
```

The directory may be absolute or relative to the current workspace directory.
Patterns must be nonempty basename globs (no directory separators or line breaks);
each must select exactly one nonempty `.dmg` file, with no duplicate selections.
All files are checked before submission and processed in pattern order, with an
18-second pause between submissions. The unchanged upstream EPP script requires
paths without whitespace and DMG basenames that do not start with `-`.

The step leaves `<dmg>.notarization.log` beside each processed image. On failure it
restores the affected original, retains `<dmg>-tonotarize` for recovery, removes
its checksum sidecars, and fails the build. Earlier successful images remain
notarized. Temporary scripts and processing directories are removed. Callers own
PR guards, timeouts, log archiving and artifact deployment.

Agent dependencies: Bash 4.4+, curl, jq, GNU-compatible md5sum, sha1sum, sha512sum,
and the standard cp, rm, mktemp, dirname, basename, head, tail, wc, cmp, sed, expr,
tee and sleep utilities. The existing EPP script is kept byte-for-byte in
`resources/macos/macosx-notarization-single.sh`, with its EPL-2.0 attribution.
No additional Jenkins plugins are required beyond the existing Pipeline and
shared-library steps. Publish this library change before enabling the callers.


## Product checksums

Call `generateProductChecksums(productsDirectory)` after macOS notarization to
validate the final products, replace previous checksums, generate SHA-256 and
SHA-512 results, verify both manifests, and archive the results in Jenkins.
The caller keeps the PR condition visible:

```groovy
stage('Generate product checksums') {
    when { expression { !github.isPullRequest() } }
    steps {
        generateProductChecksums(
            "${WORKSPACE}/releng/plugins/org.polarsys.capella.rcp.product/target/products"
        )
    }
}
```

The directory can be absolute or relative to the current workspace directory.
It must contain one nonempty Windows x86_64 ZIP, Linux x86_64 and aarch64 tar.gz,
and macOS x86_64 and aarch64 DMG. Legacy macOS tar.gz files are excluded.
The step removes previous top-level `.md5`, `.sha1`, `.sha256`, `.sha512`,
`SHA256SUMS` and `SHA512SUMS` files before generating the new results.

Each product receives `.sha256` and `.sha512` files containing the digest alone.
`SHA256SUMS` and `SHA512SUMS` list the five filenames in sorted order, with relative
names suitable for `sha256sum --check SHA256SUMS` and
`sha512sum --check SHA512SUMS`. Only successfully verified results are archived;
publication remains the caller's responsibility.

The pinned `checksum-maven-plugin:1.11` runs through a standalone library resource,
without rebuilding Capella or resolving its Tycho target platform. Temporary
scripts and the descriptor are removed on success or failure. The EPP notarizer
is unchanged; its intermediate checksums are replaced by this step.

Required agent tools: Maven, Bash 4.4+, sha256sum, sha512sum, mktemp and rm.
Publish this shared-library change before using the new call in Capella.
