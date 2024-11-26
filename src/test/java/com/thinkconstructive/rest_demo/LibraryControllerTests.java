package com.thinkconstructive.rest_demo;

import com.thinkconstructive.rest_demo.controller.LibraryController;
import com.thinkconstructive.rest_demo.model.Author;
import com.thinkconstructive.rest_demo.model.Book;
import com.thinkconstructive.rest_demo.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
class LibraryControllerTests {

	private AnnotationConfigApplicationContext context;

	@Autowired
	private LibraryController libraryController;

	@Autowired
	private LibraryService libraryService;

	@Test
	public void testFindAllBooks() {
		//Creating multiple books
		Map<String, Object> details1 = Map.of("publishing year", "1991", "genre", "Mystery");
		Map<String, Object> details2 = Map.of("publishing year", "1992", "genre", "Self-Help");
		libraryService.createBook(new Book("B011", "LLL", "MMM", details1));
		libraryService.createBook(new Book("B012", "OOO", "PPP", details2));

		//Retrieving all books
		assertThat(libraryService.getAllBooks()).hasSize(2);
	}

	@Test
	public void testDeleteBook() {
		//Creating the book to delete
		Map<String, Object> bookDetails = Map.of("publishing year", "1993", "genre", "Motivation");
		libraryService.createBook(new Book("B013", "RRR", "SSS", bookDetails));

		//Verify if the book has been created and present in the database
		Optional<Book> existingBook = libraryService.getBook("B013");
		assertThat(existingBook).isPresent();

		//deleting the book
		boolean isDeleted = libraryService.deleteBook("B013");
		assertThat(isDeleted).isTrue();

		//Verify the book no longer exists in the database
		Optional<Book> deletedBook = libraryService.getBook("B013");
		assertThat(deletedBook).isNotPresent();
	}

	@Test
	public void testUpdateBook() {
		//Creating a book to perform update operation
		Map<String, Object> initialDetails = Map.of(
				"publishing year", "1990",
				"genre", "fantasy");
		libraryService.createBook( new Book("B015", "UUU", "VVV", initialDetails));

		//Verify if the book has been created and present in the database
		Optional<Book> isCreated = libraryService.getBook("B014");
		assertThat(isCreated).isPresent();

		//Updating the book's details
		Map<String, Object> updatedDetails = Map.of(
				"publishing year", "1993",
				"genre", "Sci-Fi");
		Book updatedBook = new Book("B015", "YYY", "ZZZ", updatedDetails);
		boolean isUpdated = libraryService.updateBook(updatedBook);
		assertThat(isUpdated).isTrue();

		//Retrieving the updated book
		Optional<Book> retrievedBook= libraryService.getBook("B015");

		//Ensuring the book exists
		assertThat(retrievedBook).isPresent();

		//Verifying the changes
		assertThat(retrievedBook.get().getBookAuthor()).isEqualTo("YYY");
		assertThat(retrievedBook.get().getBookTitle()).isEqualTo("ZZZ");
		assertThat(retrievedBook.get().getBookDetail()).isEqualTo(updatedDetails);
	}

	@Test
	public void testFindAllAuthors() {
		//Creating multiple authors
		libraryService.createAuthor(new Author("A101", "AAAAA"));
		libraryService.createAuthor(new Author("A102", "BBBBB"));

		//Retrieving all authors
		assertThat(libraryService.getAllAuthors()).hasSize(2);
	}

	@Test
	public void testDeleteAuthor() {
		//Creating the author to test delete method
		libraryService.createAuthor(new Author("A103", "CCCCC"));

		//Verify if the author has been created and present
		Optional<Author> existingAuthor = libraryService.getAuthor("A103");
		assertThat(existingAuthor).isPresent();

		//Deleting the Author
		boolean isDeleted = libraryService.deleteAuthor("A103");
		assertThat(isDeleted).isTrue();

		//Verifying the author no longer exists
		Optional<Author> deletedAuthor = libraryService.getAuthor("A103");
		assertThat(deletedAuthor).isNotPresent();
	}

	@Test
	public void testUpdateAuthor() {
		//Creating an author to perform update operation
		libraryService.createAuthor(new Author("A104", "DDDDD"));

		//Verify if the author has been created and present in the database
		Optional<Author> isCreated = libraryService.getAuthor("A104");
		assertThat(isCreated).isPresent();

		//Updating the author's detail
		Author updatedAuthor = new Author("A104", "EEEEE");
		boolean isUpdated = libraryService.updateAuthor(updatedAuthor);
		assertThat(isUpdated).isTrue();

		//Retrieving the updated Author
		Optional<Author> retrievedAuthor = libraryService.getAuthor("A104");

		//Ensuring the author exists
		assertThat(retrievedAuthor).isPresent();

		//Verifying the changes
		assertThat(retrievedAuthor.get().getAuthorId()).isEqualTo("A104");
		assertThat(retrievedAuthor.get().getAuthorName()).isEqualTo("EEEEE");
	}



	void contextLoads() {
	}

}
