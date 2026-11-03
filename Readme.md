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
