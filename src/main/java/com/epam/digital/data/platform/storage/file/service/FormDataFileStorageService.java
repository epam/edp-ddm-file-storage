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

import com.epam.digital.data.platform.storage.file.dto.FileDataDto;
import com.epam.digital.data.platform.storage.file.dto.FileMetadataDto;
import com.epam.digital.data.platform.storage.file.exception.FileNotFoundException;
import com.epam.digital.data.platform.storage.file.repository.FormDataFileRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The storage service for managing form data files
 */
@Slf4j
@Builder
public class FormDataFileStorageService {

  private final FormDataFileRepository repository;
  private final FormDataFileKeyProvider keyProvider;

  /**
   * Load file by key
   *
   * @param key specified file key
   * @return file content and metadata
   * @throws FileNotFoundException if file not found
   */
  public FileDataDto loadByKey(String key) {
    log.info("Load file by key {}", key);
    var result = repository.get(key).orElseThrow(() -> new FileNotFoundException(List.of(key)));
    log.info("File was loaded by key {}", key);
    return result;
  }

  /**
   * Load file by generated key based on specified file id and process instance id.
   *
   * @param processInstanceId the process instance id to whom file attached to
   * @param id                specified file id
   * @return file content and metadata
   * @throws FileNotFoundException if file not found
   */
  public FileDataDto loadByProcessInstanceIdAndId(String processInstanceId, String id) {
    log.info("Load file by process instance id {}, file id {}", processInstanceId, id);
    var key = keyProvider.generateKey(processInstanceId, id);
    var result = repository.get(key).orElseThrow(() -> new FileNotFoundException(List.of(id)));
    log.info("File was loaded by key {}", key);
    return result;
  }

  /**
   * Save file by key
   *
   * @param key     specified storage key
   * @param content file content and metdata
   * @return metadata of the saved file
   */
  public FileMetadataDto save(String key, FileDataDto content) {
    log.info("Save file by key {}", key);
    var result = repository.put(key, content);
    log.info("File was saved by key {}", key);
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
  public FileMetadataDto save(String processInstanceId, String fileId,
      FileDataDto content) {
    log.info("Save file by process instance id {}, file id {}", processInstanceId, fileId);
    var key = keyProvider.generateKey(processInstanceId, fileId);
    return this.save(key, content);
  }

  /**
   * Get metadata by generated keys based on specified process instance id and file ids
   *
   * @param processInstanceId the process instance id to whom file ids attracted to
   * @param fileIds           specified file ids
   * @return
   * @throws FileNotFoundException if at least one file is not found
   */
  public List<FileMetadataDto> getMetadata(String processInstanceId, Set<String> fileIds) {
    log.info("Get metadata by process instance id {} and file ids {}", processInstanceId, fileIds);
    var keys = fileIds.stream()
        .map(id -> keyProvider.generateKey(processInstanceId, id)).collect(Collectors.toSet());
    var result = repository.getMetadata(keys);
    if (result.isEmpty()) {
      throw new FileNotFoundException(fileIds);
    }
    log.info("Metadata was found by keys {}", keys);
    return result;
  }

  /**
   * Delete files by process instance id.
   *
   * @param processInstanceId specified process id
   */
  public void deleteByProcessInstanceId(String processInstanceId) {
    var prefix = keyProvider.getKeyPrefixByProcessInstanceId(processInstanceId);
    log.info("Delete files by process instance id {}, files prefix {}", processInstanceId, prefix);
    var keys = repository.getKeys(prefix);
    if (!keys.isEmpty()) {
      repository.delete(keys);
      log.debug("Deleted next files from storage - {}, processInstanceId={}", keys,
          processInstanceId);
    }
  }
}
