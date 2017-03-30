package com.halfhp.lex.demo;


import io.bloco.faker.Faker;

class BookList {

    private static final Faker faker = new Faker();

    final String firstName;
    final String lastName;
    final float gpa;
    final String[] titles;

    /**
     * Create a BookList with auto generated content
     */
    public BookList() {
        firstName = faker.name.firstName();
        lastName = faker.name.lastName();
        gpa = (float) faker.number.between(2.0, 4.0);
        int bookCount = ((int) Math.round(Math.random() * 15)) + 1;
        titles = new String[bookCount];
        for(int i = 0; i < bookCount; i++) {
            titles[i] = faker.book.title();
        }
    }
}
