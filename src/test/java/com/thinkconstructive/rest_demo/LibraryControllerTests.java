package com.thinkconstructive.rest_demo;

import com.thinkconstructive.rest_demo.controller.LibraryController;
import com.thinkconstructive.rest_demo.model.Book;
import com.thinkconstructive.rest_demo.repository.BookRepository;
import com.thinkconstructive.rest_demo.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.BeforeClass;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

@SpringBootTest
class LibraryControllerTests {

	private AnnotationConfigApplicationContext context;

	@Autowired
	private LibraryController libraryController;

	@Autowired
	private LibraryService libraryService;

	@BeforeClass
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		context.scan("com.thikconstructive.rest_demo");
		context.refresh();

		libraryController = context.getBean(LibraryController.class);
		libraryService = context.getBean(LibraryService.class);


	}


	@Test
	public void testGetAllBooks() {


		List<Book> books = libraryService.getAllBooks();

		assertNotNull(books);
		assertEquals(books.size(), 5);
		assertEquals(books.get(0).getBookId(), "B001");
		assertEquals(books.get(0).getBookAuthor(), "James Clear");
		assertEquals(books.get(0).getBookTitle(), "Atomic Habits");
		assertEquals(books.get(0).getBookDetail(), "Self Help");

		assertEquals(books.get(1).getBookId(), "B002");
		assertEquals(books.get(1).getBookAuthor(), "Paulo Coelho");
		assertEquals(books.get(1).getBookTitle(), "The Alchemist");
		assertEquals(books.get(1).getBookDetail(), "Novel");

	}


	void contextLoads() {
	}

}
