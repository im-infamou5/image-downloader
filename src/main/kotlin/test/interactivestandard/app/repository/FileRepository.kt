package test.interactivestandard.app.repository

import org.springframework.data.repository.CrudRepository
import test.interactivestandard.app.entity.FileEntity

interface FileRepository : CrudRepository<FileEntity, String>