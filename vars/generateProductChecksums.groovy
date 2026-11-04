// Copyright (c) 2026 THALES GLOBAL SERVICES.
// SPDX-License-Identifier: EPL-2.0

/** Generates, verifies and archives SHA-256/SHA-512 checksums of final Capella products. */
def call(String productsDirectory) {
    if (!productsDirectory || productsDirectory.contains('\n') || productsDirectory.contains('\r')
            || productsDirectory.contains('\u0000')) {
        error('Expected a products directory')
    }
    def products = productsDirectory.startsWith('/') ? productsDirectory : "${pwd()}/${productsDirectory}"
    dir(pwd(tmp: true)) {
        def temporaryDirectory = sh(script: 'mktemp -d checksums.XXXXXXXX', returnStdout: true).trim()
        dir(temporaryDirectory) {
            try {
                writeFile file: 'checksums-pom.xml', text: libraryResource('checksums/checksums-pom.xml')
                writeFile file: 'generate-product-checksums.sh', text: libraryResource('checksums/generate-product-checksums.sh')
                withEnv(["PRODUCTS_DIRECTORY=${products}", "CHECKSUMS_POM=${pwd()}/checksums-pom.xml"]) {
                    sh 'bash generate-product-checksums.sh'
                }
            } finally {
                deleteDir()
            }
        }
    }
    dir(products) {
        archiveArtifacts artifacts: '*.sha256,*.sha512,SHA256SUMS,SHA512SUMS'
    }
}
