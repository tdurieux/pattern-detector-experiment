/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIESOR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.jpa.container.context.impl;

import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import org.apache.aries.jpa.container.context.transaction.impl.JTAEntityManager;
import org.apache.aries.jpa.container.context.transaction.impl.JTAPersistenceContextRegistry;
import org.osgi.framework.ServiceReference;
/**
 * A service factory that can lazily create persistence contexts
 */
public class ManagedPersistenceContextFactory implements EntityManagerFactory {

  private final ServiceReference emf;
  private final Map<String, Object> properties;
  private final JTAPersistenceContextRegistry registry;
    
  public ManagedPersistenceContextFactory(ServiceReference unit,
      Map<String, Object> props, JTAPersistenceContextRegistry contextRegistry) {

      emf = unit;
      properties = props;
      registry = contextRegistry;
      
  }

  public EntityManager createEntityManager() {
    EntityManagerFactory factory = (EntityManagerFactory) emf.getBundle().getBundleContext().getService(emf);
    
    PersistenceContextType type = (PersistenceContextType) properties.get(PersistenceContextManager.PERSISTENCE_CONTEXT_TYPE);
    if(type == PersistenceContextType.TRANSACTION || type == null)
      return new JTAEntityManager(factory, properties, registry);
    else {
      //TODO add support, or log the failure
      return null;
    }

  }

  public void close() {
    throw new UnsupportedOperationException();
  }
  
  public EntityManager createEntityManager(Map arg0) {
    throw new UnsupportedOperationException();
  }

  public Cache getCache() {
    throw new UnsupportedOperationException();
  }

  public CriteriaBuilder getCriteriaBuilder() {
    throw new UnsupportedOperationException();
  }

  public Metamodel getMetamodel() {
    throw new UnsupportedOperationException();
  }

  public PersistenceUnitUtil getPersistenceUnitUtil() {
    throw new UnsupportedOperationException();
  }

  public Map<String, Object> getProperties() {
    throw new UnsupportedOperationException();
  }

  public boolean isOpen() {
    throw new UnsupportedOperationException();
  }

}
