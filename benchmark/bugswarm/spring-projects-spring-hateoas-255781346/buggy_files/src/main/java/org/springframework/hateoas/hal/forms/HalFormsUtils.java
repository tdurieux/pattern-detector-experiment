/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.hateoas.hal.forms;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Greg Turnquist
 */
final class HalFormsUtils {

	public static HalFormsDocument toHalFormsDocument(Object object, ObjectMapper objectMapper) {

		if (object == null) {
			return null;
		}

		if (object instanceof HalFormsDocument) {
			return (HalFormsDocument) object;
		}

		if (object instanceof Resources) {
			return toHalFormsDocument((Resources<?>) object);
		} else if (object instanceof Resource) {
			return toHalFormsDocument((Resource<?>) object);
		} else if (object instanceof ResourceSupport) {
			return toHalFormsDocument((ResourceSupport) object);
		} else { // bean
			throw new RuntimeException("Don't know how to convert a " + object.getClass().getSimpleName() +
				" to " + HalFormsDocument.class.getSimpleName());
		}
	}

	private static HalFormsDocument toHalFormsDocument(Resources<?> resources) {

		return HalFormsDocument.halFormsDocument()
			.content(resources.getContent())
			.links(resources.getLinks())
			.templates(new ArrayList<Template>())
			.build();
	}

	/**
	 * Transform a {@link Resource} into a {@link HalFormsDocument}.
	 *
	 * @param resource
	 * @return
	 */
	private static HalFormsDocument toHalFormsDocument(Resource<?> resource) {

		return HalFormsDocument.halFormsDocument()
			.content(resource.getContent())
			.links(resource.getLinks())
			.templates(new ArrayList<Template>())
			.build();
	}

	/**
	 * Transform a {@link ResourceSupport} into a {@link HalFormsDocument}.
	 *
	 * @param rs
	 * @return
	 */
	private static HalFormsDocument toHalFormsDocument(ResourceSupport rs) {

		Map<String, Object> content = new HashMap<String, Object>();

		Set<String> propertiesToIgnore = new HashSet<String>();
		propertiesToIgnore.add("class");
		propertiesToIgnore.add("id");
		propertiesToIgnore.add("links");

		for (PropertyDescriptor descriptor : getPropertyDescriptors(rs)) {
			if (!propertiesToIgnore.contains(descriptor.getName())) {
				try {
					content.put(descriptor.getName(), descriptor.getReadMethod().invoke(rs));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return HalFormsDocument.halFormsDocument()
			.content(content)
			.links(rs.getLinks())
			.templates(new ArrayList<Template>())
			.build();
	}

	/**
	 * Look up Java bean properties for a given bean
	 *
	 * @param bean
	 * @return
	 */
	private static PropertyDescriptor[] getPropertyDescriptors(Object bean) {
		try {
			return Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
		} catch (IntrospectionException e) {
			throw new RuntimeException("failed to get property descriptors of bean " + bean, e);
		}
	}
}
