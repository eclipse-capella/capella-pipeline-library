#!/usr/bin/env bash
# Copyright (c) 2026 THALES GLOBAL SERVICES.
# SPDX-License-Identifier: EPL-2.0

set -euo pipefail
cd -- "$PRODUCTS_DIRECTORY"
shopt -s nullglob

# Validate: one nonempty file for each expected product.
require_product() {
    local pattern=$1
    local matches=( $pattern )
    if [[ ${#matches[@]} != 1 || ! -f ${matches[0]} || ! -s ${matches[0]} ]]; then
        echo "Expected exactly one nonempty product matching $pattern in $PRODUCTS_DIRECTORY" >&2
        exit 1
    fi
}
require_product 'capella-*-win32-win32-x86_64.zip'
require_product 'capella-*-linux-gtk-x86_64.tar.gz'
require_product 'capella-*-linux-gtk-aarch64.tar.gz'
require_product 'capella-*-macosx-cocoa-x86_64.dmg'
require_product 'capella-*-macosx-cocoa-aarch64.dmg'

# Clean: remove previous results, including the EPP notarizer's intermediate sums.
rm -f -- *.{md5,sha1,sha256,sha512} SHA256SUMS SHA512SUMS

# Generate: only these two goals run; the standalone POM has no Tycho parent.
mvn -B -N -f "$CHECKSUMS_POM" "-DproductsDirectory=$PRODUCTS_DIRECTORY" \
    net.nicoulaj.maven.plugins:checksum-maven-plugin:1.11:files@product-sha256 \
    net.nicoulaj.maven.plugins:checksum-maven-plugin:1.11:files@product-sha512

# Verify: fail the step before archiving or deployment if either manifest is invalid.
sha256sum --check SHA256SUMS
sha512sum --check SHA512SUMS
