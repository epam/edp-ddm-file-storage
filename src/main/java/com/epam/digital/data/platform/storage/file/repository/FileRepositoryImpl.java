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

import com.epam.digital.data.platform.integration.ceph.model.CephObjectMetadata;
import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.epam.digital.data.platform.storage.file.dto.BaseFileMetadataDto;
import com.epam.digital.data.platform.storage.file.dto.FileObjectDto;
import java.util.Map;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

  private final String cephBucketName;
  private final CephService cephService;

  @Override
  public BaseFileMetadataDto put(String key, FileObjectDto fileObjectDto) {
    var objectMetadata = cephService.put(
        cephBucketName,
        key,
        fileObjectDto.getMetadata().getContentType(),
        fileObjectDto.getMetadata().getContentLength(),
        fileObjectDto.getMetadata().getUserMetadata(),
        fileObjectDto.getContent());
    return toFileMetadataDto(objectMetadata);
  }

  @Override
  public BaseFileMetadataDto setUserMetadata(String key, Map<String, String> userMetadata) {
    var objectMetadata = cephService.setUserMetadata(
        cephBucketName, key, userMetadata);
    return toFileMetadataDto(objectMetadata);
  }

  private BaseFileMetadataDto toFileMetadataDto(CephObjectMetadata metadata) {
    return new BaseFileMetadataDto(metadata.getContentLength(), metadata.getContentType(),
        metadata.getUserMetadata());
  }
}
