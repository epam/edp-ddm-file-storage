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

import com.epam.digital.data.platform.integration.ceph.model.CephObject;
import com.epam.digital.data.platform.integration.ceph.model.CephObjectMetadata;
import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.epam.digital.data.platform.storage.file.dto.FileDataDto;
import com.epam.digital.data.platform.storage.file.dto.FileMetadataDto;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Builder
@RequiredArgsConstructor
public class CephFormDataFileRepository implements FormDataFileRepository {

    private final String cephBucketName;
    private final CephService cephService;

    @Override
    public Optional<FileDataDto> get(String key) {
        return cephService.get(cephBucketName, key).map(this::toFileDataDto);
    }

    @Override
    public FileMetadataDto put(String key, FileDataDto fileDataDto) {
        var objectMetadata = cephService.put(cephBucketName, key,
                fileDataDto.getMetadata().getContentType(), fileDataDto.getMetadata().getUserMetadata(),
                fileDataDto.getContent());
        return toFileMetadataDto(objectMetadata);
    }

    @Override
    public List<FileMetadataDto> getMetadata(Set<String> keys) {
        var result = cephService.getMetadata(cephBucketName, keys);
        return toFileMetadataDtoList(result);
    }

    @Override
    public List<FileMetadataDto> getMetadata(String prefix) {
        var result = cephService.getMetadata(cephBucketName, prefix);
        return toFileMetadataDtoList(result);
    }

    @Override
    public Set<String> getKeys(String prefix) {
        return cephService.getKeys(cephBucketName, prefix);
    }

    @Override
    public void delete(Set<String> keys) {
        cephService.delete(cephBucketName, keys);
    }

    private FileDataDto toFileDataDto(CephObject cephObject) {
        return FileDataDto.builder()
                .metadata(toFileMetadataDto(cephObject.getMetadata()))
                .content(cephObject.getContent())
                .build();
    }

    private FileMetadataDto toFileMetadataDto(CephObjectMetadata metadata) {
        return new FileMetadataDto(metadata.getContentLength(), metadata.getContentType(),
                metadata.getUserMetadata());
    }

    private List<FileMetadataDto> toFileMetadataDtoList(
            List<CephObjectMetadata> cephObjectMetadataList) {
        return cephObjectMetadataList.stream().map(this::toFileMetadataDto)
                .collect(Collectors.toList());
    }
}
