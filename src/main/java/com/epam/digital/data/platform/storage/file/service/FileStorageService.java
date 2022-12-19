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

package com.epam.digital.data.platform.storage.file.service;

import com.epam.digital.data.platform.storage.file.dto.BaseFileMetadataDto;
import com.epam.digital.data.platform.storage.file.dto.FileObjectDto;
import com.epam.digital.data.platform.storage.file.repository.FileRepository;
import java.util.Map;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * The storage service for managing files
 */
@Slf4j
@Builder
public class FileStorageService {

  private final FileRepository repository;
  private final FormDataFileKeyProvider keyProvider;

  /**
   * Save file by key
   *
   * @param key     specified storage key
   * @param content file content and metdata
   * @return metadata of the saved file
   */
  public BaseFileMetadataDto save(String key, FileObjectDto content) {
    log.info("Save file with key {}", key);
    var result = repository.put(key, content);
    log.info("File was saved with key {}", key);
    return result;
  }

  /**
   * Save by generated key based on specified process instance id and file id
   *
   * @param processInstanceId the process instance id to whom file attached to
   * @param fileId            specified file id
   * @param content           file content and metadata
   * @return metadata of the saved file
   */
  public BaseFileMetadataDto save(String processInstanceId, String fileId, FileObjectDto content) {
    log.info("Save file by process instance id {}, file id {}", processInstanceId, fileId);
    var key = keyProvider.generateKey(processInstanceId, fileId);
    return this.save(key, content);
  }

  /**
   * Sets a new userMetadata to a file with the current key
   *
   * @param key          object id.
   * @param userMetadata file content and metadata
   * @return metadata of the saved file
   */
  public BaseFileMetadataDto setUserMetadata(String key, Map<String, String> userMetadata) {
    log.info("Set user metadata to file with key {}", key);
    var result = repository.setUserMetadata(key, userMetadata);
    log.info("Metadata saved {}", key);
    return result;
  }

  /**
   * Sets a new userMetadata to a file with the current key
   *
   * @param processInstanceId the process instance id to whom file attached to
   * @param fileId            specified file id
   * @param userMetadata      file content and metadata
   * @return metadata of the saved file
   */
  public BaseFileMetadataDto setUserMetadata(String processInstanceId, String fileId,
      Map<String, String> userMetadata) {
    log.info("Set user metadata to file by process instance id {}, file id {}", processInstanceId,
        fileId);
    var key = keyProvider.generateKey(processInstanceId, fileId);
    return repository.setUserMetadata(key, userMetadata);
  }
}
