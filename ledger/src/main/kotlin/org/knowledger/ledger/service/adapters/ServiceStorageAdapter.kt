package org.knowledger.ledger.service.adapters

import org.knowledger.ledger.adapters.EagerStorable
import org.knowledger.ledger.core.storage.adapters.SchemaProvider
import org.knowledger.ledger.service.ServiceClass

interface ServiceStorageAdapter<T : ServiceClass> : ServiceLoadable<T>,
                                                    EagerStorable<T>,
                                                    SchemaProvider<T>