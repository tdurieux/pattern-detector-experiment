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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Greg Turnquist
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class HalFormsWebMvcTest {

	@Autowired
	WebApplicationContext context;

	MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = webAppContextSetup(this.context)
			.build();
	}

	@Test
	public void basic() throws Exception {

//		this.mockMvc.perform(get("/employees").accept(MediaTypes.HAL_FORMS_JSON))
//			.andDo(print())
//			.andExpect(status().isOk());

		this.mockMvc.perform(get("/employees/0").accept(MediaTypes.HAL_FORMS_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._links.*", hasSize(4)))
			.andExpect(jsonPath("$._links['self'].href", is("http://localhost/employees/0")))
			.andExpect(jsonPath("$._links['put'].href", is("http://localhost/employees/0")))
			.andExpect(jsonPath("$._links['patch'].href", is("http://localhost/employees/0")))
			.andExpect(jsonPath("$._links['employees'].href", is("http://localhost/employees")))
			.andExpect(jsonPath("$._templates.*", hasSize(1)));
	}

	@RestController
	static class EmployeeController {

		private final static Map<Integer, Employee> EMPLOYEES = new TreeMap<Integer, Employee>();

		static {
			EMPLOYEES.put(0, new Employee("Frodo Baggins", "ring bearer"));
			EMPLOYEES.put(1, new Employee("Bilbo Baggins", "burglar"));
		}

		@GetMapping("/employees")
		public Resources<Resource<Employee>> all() {

			// Create a list of Resource<Employee>'s to return
			List<Resource<Employee>> employees = new ArrayList<Resource<Employee>>();

			// Fetch each Resource<Employee> using the controller's findOne method.
			for (int i=0; i < EMPLOYEES.size(); i++) {
				employees.add(findOne(String.valueOf(i)));
			}

			// Generate an "Affordance" based on this method (the "self" link)
			Link selfLink = linkTo(methodOn(EmployeeController.class).all()).withSelfRel();

			// Return the collection of employee resources along with the composite affordance
			return new Resources<Resource<Employee>>(employees, selfLink);
		}

		@GetMapping("/employees/{id}")
		public Resource<Employee> findOne(@PathVariable String id) {

			// Start the affordance with the "self" link, i.e. this method.
			Link findOneLink =
				linkTo(methodOn(EmployeeController.class).findOne(id)).withSelfRel();

			// Define another affordance for PUT
			Link updateLink =
				linkTo(methodOn(EmployeeController.class).updateEmployee(null, id)).withRel("put");

			// Define a third affordance for PATCH
			Link partiallyUpdateLink =
				linkTo(methodOn(EmployeeController.class).partiallyUpdateEmployee(null, id)).withRel("patch");

			// Return the affordance + a link back to the entire collection resource.
			return new Resource<Employee>(
				EMPLOYEES.get(Integer.parseInt(id)),
				findOneLink,
				updateLink,
				partiallyUpdateLink,
				linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
		}

		@PostMapping("/employees")
		public ResponseEntity<?> newEmployee(@RequestBody Employee employee) {

			int newEmployeeId = EMPLOYEES.size();

			EMPLOYEES.put(newEmployeeId, employee);

			try {
				return ResponseEntity
					.noContent()
					.location(new URI(findOne(String.valueOf(newEmployeeId)).getLink(Link.REL_SELF).expand().getHref()))
					.build();
			} catch (URISyntaxException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}

		@PutMapping("/employees/{id}")
		public ResponseEntity<?> updateEmployee(@RequestBody Employee employee, @PathVariable String id) {

			EMPLOYEES.put(Integer.parseInt(id), employee);
			try {
				return ResponseEntity
					.noContent()
					.location(new URI(findOne(id).getLink(Link.REL_SELF).expand().getHref()))
					.build();
			} catch (URISyntaxException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}

		@PatchMapping("/employees/{id}")
		public ResponseEntity<?> partiallyUpdateEmployee(@RequestBody Employee employee, @PathVariable String id) {

			Employee oldEmployee = EMPLOYEES.get(id);

			Employee newEmployee = oldEmployee;

			if (employee.getName() != null) {
				newEmployee = newEmployee.withName(employee.getName());
			}

			if (employee.getRole() != null) {
				newEmployee = newEmployee.withRole(employee.getRole());
			}

			EMPLOYEES.put(Integer.parseInt(id), newEmployee);
			try {
				return ResponseEntity
					.noContent()
					.location(new URI(findOne(id).getLink(Link.REL_SELF).expand().getHref()))
					.build();
			} catch (URISyntaxException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}

	@Configuration
	@EnableWebMvc
	@EnableHypermediaSupport(type = {HypermediaType.HAL_FORMS})
	static class TestConfig {

		@Bean
		EmployeeController employeeController() {
			return new EmployeeController();
		}

	}


}
