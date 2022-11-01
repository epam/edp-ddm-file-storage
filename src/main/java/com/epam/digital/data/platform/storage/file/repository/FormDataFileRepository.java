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

package com.epam.digital.data.platform.storage.file.repository;

import com.epam.digital.data.platform.storage.file.dto.FileDataDto;
import com.epam.digital.data.platform.storage.file.dto.FileMetadataDto;
import org.springframework.cloud.sleuth.annotation.NewSpan;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The repository for getting and storing files.
 */
public interface FormDataFileRepository {

    /**
     * Retrieve file data by key
     *
     * @param key file key
     * @return {@link FileDataDto} content and metadata representation (optional)
     */
    @NewSpan("getFile")
    Optional<FileDataDto> get(String key);


    /**
     * Put file data to repository
     *
     * @param key         specified file key
     * @param fileDataDto {@link FileDataDto} content and metadata representation
     */
    @NewSpan("putFile")
    FileMetadataDto put(String key, FileDataDto fileDataDto);

    /**
     * Get files metadata by keys
     *
     * @param keys specified file keys
     * @return list of metadata
     */
    @NewSpan("getFilesMetadata")
    List<FileMetadataDto> getMetadata(Set<String> keys);

    /**
     * Get files metadata by prefix
     *
     * @param prefix specified prefix
     * @return list of metadata
     */
    @NewSpan("getFilesMetadata")
    List<FileMetadataDto> getMetadata(String prefix);

    /**
     * Get storage keys by prefix
     *
     * @param prefix srovided prefix
     * @return list of storage keys
     */
    @NewSpan("getKeysByPrefix")
    Set<String> getKeys(String prefix);

    /**
     * Delete files by set of storage keys]
     *
     * @param keys provided storage keys
     */
    @NewSpan("deleteFilesByKeys")
    void delete(Set<String> keys);
}
