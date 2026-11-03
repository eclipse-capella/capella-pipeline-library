#!/usr/bin/env bash
# Copyright (c) 2026 THALES GLOBAL SERVICES.
# SPDX-License-Identifier: EPL-2.0

set -euo pipefail
products=$(cd -- "$PRODUCTS_DIRECTORY" && pwd)
notarizer="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)/macosx-notarization-single.sh"
for tool in curl jq md5sum sha1sum sha512sum; do
    command -v "$tool" >/dev/null || { echo "$tool is required" >&2; exit 1; }
done
# The unchanged EPP script splits its input path on whitespace.
[[ $products != *[[:space:]]* ]] || { echo "EPP notarization requires a products path without whitespace" >&2; exit 1; }
shopt -s nullglob
# Expand caller-provided globs without word splitting or evaluating shell code.
IFS=
dmgs=()
while read -r pattern; do
    [[ -n $pattern && $pattern != */* && $pattern != *$'\r'* ]] || { echo "Expected a nonempty DMG basename pattern" >&2; exit 1; }
    matches=("$products"/$pattern)
    if [[ ${#matches[@]} != 1 || ! -f ${matches[0]} || ! -s ${matches[0]} ]]; then
        echo "Expected exactly one nonempty DMG matching $pattern in $products" >&2
        exit 1
    fi
    dmg=${matches[0]}
    [[ $dmg == *.dmg && $dmg != *[[:space:]]* && $(basename -- "$dmg") != -* ]] || { echo "Unsupported EPP DMG filename: $dmg" >&2; exit 1; }
    for previous in "${dmgs[@]}"; do
        [[ $previous != "$dmg" ]] || { echo "DMG matched more than once: $dmg" >&2; exit 1; }
    done
    dmgs+=("$dmg")
done <<< "$DMG_PATTERNS"
for index in "${!dmgs[@]}"; do
    dmg=${dmgs[$index]}
    cp -- "$dmg" "$dmg-tonotarize"
    work=$(mktemp -d "$products/notarization.XXXXXX")
    (
        # Keep recovery data outside the files removed by the unmodified EPP script.
        cp -- "$dmg" "$work/original.dmg"
        notarized=false
        trap 'if [[ $notarized != true ]]; then cp -- "$work/original.dmg" "$dmg"; cp -- "$work/original.dmg" "$dmg-tonotarize"; rm -f -- "$dmg.md5" "$dmg.sha1" "$dmg.sha512"; fi; rm -rf -- "$work"' EXIT
        cd "$work"
        bash "$notarizer" "$dmg"
        result="$work/$(basename "$dmg")"
        [[ -s $result ]] || { echo "No downloaded DMG: $dmg" >&2; exit 1; }
        # UDIF disk images end with a 512-byte trailer beginning with "koly".
        [[ $(wc -c < "$result") -ge 512 && $(tail -c 512 "$result" | head -c 4) == koly ]] || { echo "Invalid downloaded DMG: $dmg" >&2; exit 1; }
        cmp -- "$result" "$dmg"
        cd "$products"
        for checksum in md5 sha1 sha512; do
            "${checksum}sum" --check "$(basename "$dmg").$checksum"
        done
        notarized=true
    ) 2>&1 | tee "$dmg.notarization.log"
    if (( index + 1 < ${#dmgs[@]} )); then
        sleep 18
    fi
done
