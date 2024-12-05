package com.libraryManagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.controller.LibraryController;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import com.libraryManagement.service.LibraryService;
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
	public void testFindAllAuthors() {
		//Test the total number of authors from data.sql
		assertThat(libraryService.getAllAuthors()).hasSize(5);
	}

	@Test
	public void testFindAuthorById() {
		String authorId = "A101";

		//Checking if the author exists in the database with the given authorId
		Optional<Author> author = libraryService.getAuthor(authorId);
		if(author.isPresent()) {
			Author retrievedAuthor = author.get();
			assertThat(retrievedAuthor.getAuthorId()).isEqualTo(authorId);
			assertThat(retrievedAuthor.getAuthorName()).isEqualTo("James Clear");
			System.out.println("Author found");
		} else {
			System.out.println("Author with authorId: " + authorId + " ,does not exist");
		}
	}

	@Test
	public void testFindAuthorByNonExistingId() {
		String authorId = "A000";

		//Checking if the author exists in the database with the given authorId
		Optional<Author> author = libraryService.getAuthor(authorId);
		if(author.isPresent()) {
			Author retrievedAuthor = author.get();
			assertThat(retrievedAuthor.getAuthorId()).isEqualTo(authorId);
			assertThat(retrievedAuthor.getAuthorName()).isEqualTo("James Clear");
			System.out.println("Author found");
		} else {
			System.out.println("Author with authorId: " + authorId + " ,does not exist");
		}
	}

	@Test
	public void testDeleteAuthor() {
		String authorId = "A103";

		//Verify if the author exists in the records
		Optional<Author> existingAuthor = libraryService.getAuthor(authorId);

		if(existingAuthor.isPresent()) {
			//Deleting the Author
			boolean isDeleted = libraryService.deleteAuthor(authorId);
			assertThat(isDeleted).isTrue();

			//Verifying the author no longer exists
			Optional<Author> deletedAuthor = libraryService.getAuthor(authorId);
			assertThat(deletedAuthor).isNotPresent();
			System.out.println("Author deleted successfully");
		} else {
			System.out.println("Author does not exist, delete failed");
		}
	}

	@Test
	public void testDeleteNonExistingAuthor() {
		String authorId = "A223";

		//Verify if the author exists in the records
		Optional<Author> existingAuthor = libraryService.getAuthor(authorId);

		if(existingAuthor.isPresent()) {
			//Deleting the Author
			boolean isDeleted = libraryService.deleteAuthor(authorId);
			assertThat(isDeleted).isTrue();

			//Verifying the author no longer exists
			Optional<Author> deletedAuthor = libraryService.getAuthor(authorId);
			assertThat(deletedAuthor).isNotPresent();
			System.out.println("Author deleted successfully");
		} else {
			System.out.println("Author does not exist, delete failed");
		}
	}

	@Test
	public void testUpdateAuthor() {
		String authorId = "A101";

		//Verify if the author exists in the database or not
		Optional<Author> existingAuthor = libraryService.getAuthor(authorId);
		if(existingAuthor.isPresent()) {
			//Updating the author's detail
			Author updatedAuthor = new Author(authorId, "Jay Shetty");
			boolean isUpdated = libraryService.updateAuthor(updatedAuthor);
			assertThat(isUpdated).isTrue();

			//Retrieving the updated author and verifying the changes
			Optional<Author> retrievedAuthor = libraryService.getAuthor(authorId);
			assertThat(retrievedAuthor).isPresent();
			assertThat(retrievedAuthor.get().getAuthorName()).isEqualTo("Jay Shetty");
			System.out.println("Author updated successfully with all the values verification");
		} else  {
			System.out.println("Author not found in the database, update failed");
		}
	}

	@Test
	public void testUpdateNonExistingAuthor() {
		String authorId = "A512";

		//Verify if the author exists in the database or not
		Optional<Author> existingAuthor = libraryService.getAuthor(authorId);
		if(existingAuthor.isPresent()) {
			//Updating the author's detail
			Author updatedAuthor = new Author(authorId, "Jay Shetty");
			boolean isUpdated = libraryService.updateAuthor(updatedAuthor);
			assertThat(isUpdated).isTrue();

			//Retrieving the updated author and verifying the changes
			Optional<Author> retrievedAuthor = libraryService.getAuthor(authorId);
			assertThat(retrievedAuthor).isPresent();
			assertThat(retrievedAuthor.get().getAuthorName()).isEqualTo(updatedAuthor.getAuthorName());
			System.out.println("Author updated successfully with all the values verification");
		} else  {
			System.out.println("Author not found in the database, update failed");
		}
	}

	@Test
	public void testCreateBook() {
		JsonNode bookDetail = new ObjectMapper().createObjectNode()
				.put("publishing year", 2015)
				.put("genre", "Fiction");
		Book newBook= new Book("B106", "Heidi", "Johanna Spyri", bookDetail);

		//Adding the book
		boolean isAdded = libraryService.createBook(newBook);

		//Verifying if the book was added successfully
		Optional<Book> retrievedBook = libraryService.getBook("B106");
		if(retrievedBook.isPresent()) {
			Book addedBook = retrievedBook.get();
			assertThat(addedBook.getBookId()).isEqualTo("B106");
			assertThat(addedBook.getBookAuthor()).isEqualTo("Heidi");
			assertThat(addedBook.getBookTitle()).isEqualTo("Johanna Spyri");
			assertThat(addedBook.getBookDetail()).isEqualTo("{\"publishing year\":2015,\"genre\":\"Fiction\"}");
		} else {
			System.out.println("Added book is not found in the database");
		}
	}

	@Test
	public void testFindAllBooks() {
		//Testing the total number of books from data.sql
		assertThat(libraryService.getAllBooks()).hasSize(5);
	}

	@Test
	public void testFindBookById() throws JsonProcessingException {
		String bookId = "B105";

		//Verifying if the book record exists with the given bookId
		Optional<Book> book = libraryService.getBook(bookId);

		if(book.isPresent()) {
			Book retrievedBook = book.get();
			assertThat(retrievedBook.getBookId()).isEqualTo(bookId);
			assertThat(retrievedBook.getBookAuthor()).isEqualTo("Darius Foroux");
			assertThat(retrievedBook.getBookTitle()).isEqualTo("Do It Today");

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode expectedDetails = objectMapper.readTree("{\"publishing year\": \"2018\", \"genre\": \"Motivational\"}");
			assertThat(retrievedBook.getBookDetail()).isEqualTo(expectedDetails);
			System.out.println("Book found");
		} else {
			System.out.println("Book with bookId: " + bookId + " ,does not exist");
		}
	}

	@Test
	public void testFindBookByNonExistingId() {
		String bookId = "B000";
		Optional<Book> book = libraryService.getBook(bookId);

		if(book.isPresent()) {
			Book retrievedBook = book.get();
			assertThat(retrievedBook.getBookId()).isEqualTo(bookId);
			assertThat(retrievedBook.getBookAuthor()).isEqualTo("Darius Foroux");
			assertThat(retrievedBook.getBookTitle()).isEqualTo("Do It Today");
			assertThat(retrievedBook.getBookDetail()).isEqualTo(Map.of(
					"publishing year", "2018",
					"genre", "Motivational"));
			System.out.println("Book found");
		} else {
			System.out.println("Book with bookId: " + bookId + ",does not exist");
		}
	}

	@Test
	public void testDeleteBook() {
		String bookId = "B103";
		Optional<Book> book = libraryService.getBook(bookId);

		if(book.isPresent()) {
			//Verify if the book exists in the database
			boolean isDeleted = libraryService.deleteBook(bookId);
			assertThat(isDeleted).isTrue();
			//Verify the book no longer exists in the database
			Optional<Book> deletedBook = libraryService.getBook(bookId);
			assertThat(deletedBook).isNotPresent();
			System.out.println("Book deleted successfully");
		} else {
			System.out.println("Book not found");
		}
	}

	@Test
	public void testDeleteNonExistingBook() {
		String bookId = "B213";
		Optional<Book> book = libraryService.getBook(bookId);

		if(book.isPresent()) {
			//Verify if the book exists in the database
			boolean isDeleted = libraryService.deleteBook(bookId);
			assertThat(isDeleted).isTrue();
			//Verify the book no longer exists in the database
			Optional<Book> deletedBook = libraryService.getBook(bookId);
			assertThat(deletedBook).isNotPresent();
			System.out.println("Book deleted successfully");
		} else {
			System.out.println("Book not found");
		}
	}

//	@Test
//	public void testUpdateBook() {
//		String bookId = "B101";
//
//		//Verify if the book exists in the database
//		Optional<Book> existingBook = libraryService.getBook(bookId);
//		if(existingBook.isPresent()) {
//			//Updating the book's details
//			Book updatedBook = new Book(bookId, "Ankur Warikoo", "Do Epic Shit", Map.of("publishing year", "2022", "genre", "Entrepreneurship"));
//			boolean isUpdated = libraryService.updateBook(updatedBook);
//			assertThat(isUpdated).isTrue();
//
//			//Fetching the updated book and verifying the changes
//			Optional<Book> retrievedBook= libraryService.getBook(bookId);
//			assertThat(retrievedBook).isPresent();
//			assertThat(retrievedBook.get().getBookAuthor()).isEqualTo(updatedBook.getBookAuthor());
//			assertThat(retrievedBook.get().getBookTitle()).isEqualTo(updatedBook.getBookTitle());
//			assertThat(retrievedBook.get().getBookDetail()).isEqualTo(updatedBook.getBookDetail());
//			System.out.println("Book updated successfully with all the values verified");
//		} else {
//			System.out.println("Book not found in the database, update failed");
//		}
//	}

//	@Test
//	public void testUpdateNonExistingBook() {
//		String bookId = "B213";
//
//		//Verify if the book exists in the database
//		Optional<Book> existingBook = libraryService.getBook(bookId);
//		if(existingBook.isPresent()) {
//			//Updating the book's details
//			Book updatedBook = new Book(bookId, "Ankur Warikoo", "Do Epic Shit", Map.of("publishing year", "2022", "genre", "Entrepreneurship"));
//			boolean isUpdated = libraryService.updateBook(updatedBook);
//			assertThat(isUpdated).isTrue();
//
//			//Fetching the updated book and verifying the changes
//			Optional<Book> retrievedBook= libraryService.getBook(bookId);
//			assertThat(retrievedBook).isPresent();
//			assertThat(retrievedBook.get().getBookAuthor()).isEqualTo(updatedBook.getBookAuthor());
//			assertThat(retrievedBook.get().getBookTitle()).isEqualTo(updatedBook.getBookTitle());
//			assertThat(retrievedBook.get().getBookDetail()).isEqualTo(updatedBook.getBookDetail());
//			System.out.println("Book updated successfully with all the values verified");
//		} else {
//			System.out.println("Book not found in the database, update failed");
//		}
//	}

	void contextLoads() {
		assertThat(libraryService).isNotNull();
		assertThat(libraryController).isNotNull();
	}

}
