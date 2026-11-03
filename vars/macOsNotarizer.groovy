// Copyright (c) 2026 THALES GLOBAL SERVICES.
// SPDX-License-Identifier: EPL-2.0

/** Notarizes one nonempty DMG per basename pattern, in caller-specified order. */
def call(String productsDirectory, List<String> dmgPatterns) {
    if (!productsDirectory || productsDirectory.contains('\n') || productsDirectory.contains('\r')
            || productsDirectory.contains('\u0000')) {
        error('Expected a products directory')
    }
    if (!dmgPatterns || dmgPatterns.any { !(it instanceof String) || !it
            || it.contains('/') || it.contains('\n') || it.contains('\r') || it.contains('\u0000') }) {
        error('Expected a nonempty list of DMG basename patterns')
    }
    // Resolve relative products paths before changing to the temporary directory.
    def products = productsDirectory.startsWith('/') ? productsDirectory : "${pwd()}/${productsDirectory}"
    dir(pwd(tmp: true)) {
        def temporaryDirectory = sh(script: 'mktemp -d notarizer.XXXXXXXX', returnStdout: true).trim()
        dir(temporaryDirectory) {
            try {
                for (resource in ['macosx-notarization-single.sh', 'notarize-products.sh']) {
                    writeFile file: resource, text: libraryResource("macos/${resource}")
                }
                withEnv(["PRODUCTS_DIRECTORY=${products}", "DMG_PATTERNS=${dmgPatterns.join('\n')}"]) {
                    sh 'bash notarize-products.sh'
                }
            } finally {
                deleteDir()
            }
        }
    }
}
