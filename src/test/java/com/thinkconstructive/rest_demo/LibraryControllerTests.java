package com.thinkconstructive.rest_demo;

import com.thinkconstructive.rest_demo.controller.LibraryController;
import com.thinkconstructive.rest_demo.model.Author;
import com.thinkconstructive.rest_demo.model.Book;
import com.thinkconstructive.rest_demo.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class LibraryControllerTests {

	@Autowired
	private LibraryController libraryController;

	@Autowired
	private LibraryService libraryService;

	@Test
	public void testFindAllBooks() {
		//Testing the total number of books from data.sql
		assertThat(libraryService.getAllBooks()).hasSize(3);
	}

	@Test
	public void testDeleteBook() {
		boolean isDeleted = libraryService.deleteBook("B013");
		assertThat(isDeleted).isTrue();

		//Verify the book no longer exists in the database
		Optional<Book> deletedBook = libraryService.getBook("B013");
		assertThat(deletedBook).isNotPresent();
	}

	@Test
	public void testUpdateBook() {
		//Updating the book's details
		Book updatedBook = new Book("B011", "YYY", "ZZZ", Map.of("publishing year", 2000));
		boolean isUpdated = libraryService.updateBook(updatedBook);
		assertThat(isUpdated).isTrue();

		//Verifying the changes
		Optional<Book> retrievedBook= libraryService.getBook("B011");
		//Ensuring the book exists
		assertThat(retrievedBook).isPresent();
		assertThat(retrievedBook.get().getBookAuthor()).isEqualTo("YYY");
		assertThat(retrievedBook.get().getBookTitle()).isEqualTo("ZZZ");
	}

	@Test
	public void testFindAllAuthors() {
		//Test the total number of authors from data.sql
		assertThat(libraryService.getAllAuthors()).hasSize(2);
	}

	@Test
	public void testDeleteAuthor() {
		//Deleting the Author
		boolean isDeleted = libraryService.deleteAuthor("A102");
		assertThat(isDeleted).isTrue();

		//Verifying the author no longer exists
		Optional<Author> deletedAuthor = libraryService.getAuthor("A102");
		assertThat(deletedAuthor).isNotPresent();
	}

	@Test
	public void testUpdateAuthor() {
		//Updating the author's detail
		Author updatedAuthor = new Author("A101", "CCCCC");
		boolean isUpdated = libraryService.updateAuthor(updatedAuthor);
		assertThat(isUpdated).isTrue();

		//Verifying the changes
		Optional<Author> retrievedAuthor = libraryService.getAuthor("A104");
		assertThat(retrievedAuthor).isPresent();
		assertThat(retrievedAuthor.get().getAuthorName()).isEqualTo("CCCCC");
	}



	void contextLoads() {
		assertThat(libraryService).isNotNull();
		assertThat(libraryController).isNotNull();
	}

}
