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

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseFileMetadataDto {

  private long contentLength;
  private String contentType;
  protected Map<String, String> userMetadata = new LinkedHashMap<>();

  public BaseFileMetadataDto(long contentLength, String contentType,
      Map<String, String> userMetadata) {
    this.contentLength = contentLength;
    this.contentType = contentType;
    this.userMetadata = userMetadata;
  }

  public String getId() {
    return userMetadata.get(BaseUserMetadataHeaders.ID);
  }

  public void setId(String id) {
    setIfNotNull(userMetadata, BaseUserMetadataHeaders.ID, id);
  }

  public String getChecksum() {
    return userMetadata.get(BaseUserMetadataHeaders.CHECKSUM);
  }

  public void setChecksum(String checksum) {
    setIfNotNull(userMetadata, BaseUserMetadataHeaders.CHECKSUM, checksum);
  }

  public String getFilename() {
    return userMetadata.get(BaseUserMetadataHeaders.FILENAME);
  }

  public void setFilename(String filename) {
    setIfNotNull(userMetadata, BaseUserMetadataHeaders.FILENAME, filename);
  }

  public void setIfNotNull(Map<String, String> map, String key, String value) {
    if (key != null && value != null) {
      map.put(key, value);
    }
  }

  @Getter
  public static class BaseUserMetadataHeaders {

    public static final String ID = "id";
    public static final String CHECKSUM = "checksum";
    public static final String FILENAME = "filename";
  }
}
