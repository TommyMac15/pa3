#!/bin/bash

# Directory to traverse
DIRECTORY="/home/tommyboymac/cnt4731/CNT4731_PA3/pa3/pa3/video"
OUTPUT_DIRECTORY="/home/tommyboymac/cnt4731/CNT4731_PA3/pa3/pa3/compressed_video"

# Create the output directory if it doesn't exist
mkdir -p "$OUTPUT_DIRECTORY"

# Find all .jpg files and compress them
find "$DIRECTORY" -type f -name "*.jpg" | while read -r FILE; do
    OUTPUT_FILE="$OUTPUT_DIRECTORY/compressed_$(basename "$FILE")"
    convert "$FILE" -quality 70 "$OUTPUT_FILE"
    echo "Compressed $FILE to $OUTPUT_FILE"
done