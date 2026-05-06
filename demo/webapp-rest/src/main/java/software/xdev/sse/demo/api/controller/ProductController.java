package software.xdev.sse.demo.api.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.xdev.sse.demo.api.services.ProductService;
import software.xdev.sse.demo.entities.Product;


@RestController
@RequestMapping("/api/products")
public record ProductController(ProductService productService)
{
	@GetMapping("/all")
	public List<Product> getAll()
	{
		return this.productService.getAll();
	}
	
	@GetMapping("/{id}")
	public Product getByName(@PathVariable("id") final long id)
	{
		return this.productService.getById(id);
	}
	
	@GetMapping("/name/{name}")
	public Product getByName(@PathVariable("name") final String name)
	{
		return this.productService.getByName(name);
	}
	
	@DeleteMapping("/{id}")
	public HttpEntity<Object> deleteById(@PathVariable("id") final long id)
	{
		final boolean deleted = this.productService.deleteById(id);
		return new ResponseEntity<>(HttpStatusCode.valueOf(deleted
			? HttpServletResponse.SC_OK
			: HttpServletResponse.SC_NOT_FOUND));
	}
	
	@PostMapping
	public HttpEntity<Object> create(
		@Valid @NotNull @RequestBody final ProductService.ProductCreateDTO dto)
	{
		final Product created = this.productService.create(dto);
		if(created == null)
		{
			return new ResponseEntity<>(
				"Product already exists",
				HttpStatusCode.valueOf(HttpServletResponse.SC_CONFLICT));
		}
		return new HttpEntity<>(created);
	}
	
	@PutMapping
	public HttpEntity<Object> update(
		@Valid @NotNull @RequestBody final ProductService.ProductUpdateDTO dto)
	{
		final Product created = this.productService.update(dto);
		if(created == null)
		{
			return new ResponseEntity<>(HttpStatusCode.valueOf(HttpServletResponse.SC_NOT_FOUND));
		}
		return new HttpEntity<>(created);
	}
}
