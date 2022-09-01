/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.epam.digital.data.platform.storage.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class FileMetadataDto {

    private Long contentLength;
    private String contentType;
    private Map<String, String> userMetadata = new LinkedHashMap<>();

    public FileMetadataDto(String contentType, Map<String, String> userMetadata) {
        this.contentType = contentType;
        this.userMetadata = userMetadata;
    }

    @Builder
    public FileMetadataDto(Long contentLength, String contentType, String id, String checksum,
                           String filename) {
        this.contentLength = contentLength;
        this.contentType = contentType;
        userMetadata.put(UserMetadataHeaders.ID.getValue(), id);
        userMetadata.put(UserMetadataHeaders.CHECKSUM.getValue(), checksum);
        userMetadata.put(UserMetadataHeaders.FILENAME.getValue(), filename);
    }

    public String getId() {
        return userMetadata.get(UserMetadataHeaders.ID.getValue());
    }

    public String getChecksum() {
        return userMetadata.get(UserMetadataHeaders.CHECKSUM.getValue());
    }

    public String getFilename() {
        return userMetadata.get(UserMetadataHeaders.FILENAME.getValue());
    }

    @Getter
    @RequiredArgsConstructor
    public enum UserMetadataHeaders {

        ID("id"),
        CHECKSUM("checksum"),
        FILENAME("filename");

        private final String value;

    }
}
