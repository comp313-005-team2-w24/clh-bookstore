package io.clh.bookstore.categories;

import io.clh.models.Book;
import io.clh.models.Category;

import java.util.List;

public interface ICategory {
    List<Book> GetAllBooksByCategory (Integer id);
    Category AddCategory (Category category);
    Category DeleteCategory (Integer categoryId);
    List<Category> GetAllCategories ();
    Category UpdateCategory (Category category);
}
