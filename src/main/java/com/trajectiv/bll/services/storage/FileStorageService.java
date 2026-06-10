package com.trajectiv.bll.services.storage;

import com.trajectiv.bll.dto.storage.StorageCommandBllDto;
import com.trajectiv.bll.dto.storage.StoredFileBllDto;

public interface FileStorageService {

    StoredFileBllDto store(StorageCommandBllDto command);

    void delete(String storageKey);
}
