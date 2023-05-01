# Tests
### Test 1: Creating an account
1. User launches program
2. User enters hostname (localhost)
3. User selects customer as type of user
4. User selects create account
5. User enters username and then password
6. User logs in using their username and password

Expected result: Program connects to server, creates the account, and logs into account.

Test Status: Passed
### Test 2: Purchasing product
1. User launches program
2. User enters hostname (localhost)
3. User selects customer as type of user
4. User logs in using their username and password
5. User selects a product to visit its product page
6. User selects purchase product, and enters the amount to purchase
7. User returns to main page and selects “view purchase history”, purchase history for the product should appear at the top
8. User returns to main page and exits

Expected Result: Program connects to server, logs into the account, 
Goes to the product page and purchases the amount requested, and finally exits out of the program.

Test Status: Passed
### Test 3: Adding product to cart
1. User launches program
2. User enters hostname (localhost)
3. User selects customer as type of user
4. User logs in using their username and password
5. User selects a product to visit its product page
6. User selects add product to cart, and enters the amount to purchase
7. User returns to main page and selects “view shopping cart”, added product should appear in the shopping cart
8. User returns to main page and exits

Expected Result: Program connects to server, logs into the account, 
Goes to the product page and adds the amount requested to shopping cart, and finally exits out of the program.

Test Status: Passed
### Test 4: Search specific product(s)
1. User launches program
2. User enters hostname (localhost)
3. User selects customer as type of user
4. User logs in using their username and password
6. User selects search function, and enters the term for searching
7. List of products containing should be in the dropdown and available to choose
8.User selects a product and is taken to the product’s page
9. User returns to main page and exits

Expected Result: Program connects to server, logs into the account, 
Searches for required products can access product page of these products, and finally exits out of the program.

Test Status: Passed
### Test 5: Sort marketplace
1. User launches program
2. User enters hostname (localhost)
3. User selects customer as type of user
4. User logs in using their username and password
5. User selects “Sort the marketplace on price”
6. Products are sorted by price (low to high) and available to choose and view product’s page
7. User returns to main page and selects “Sort the marketplace on quantity available.”
8. Products are sorted by quantity available (high to low) and available to choose and view product’s page
9. User returns to main page and exits

Expected Result: Program connects to server, logs into the account, 
Sorts products as required, and finally exits out of the program.

Test Status: Passed
### Test 6: View Dashboard
1. User launches program
2. User enters hostname (localhost)
3. User selects customer as type of user
4. User logs in using their username and password
5. User selects “View a dashboard with store and seller information.”, selects sorting by products sold, then selects a sorting option, for example low-high
6. A list of stores is sorted by their sales low-high.
7.User returns to main page and repeats step 5 with different sorting options. 
8.User returns to main page, selects “View a dashboard with store and seller information.”, selects sorting by “products purchased by you”, then selects a sorting option, for example low-high.
9. A list of stores is sorted low-high by number of products the user purchased in this store.
10.User returns to main page and repeats step 8 with different sorting options. 
11.User returns to main page and exits.

Expected Result: Program connects to server, logs into the account, 
Sorts stores by products sold or by number of products the user purchased in this store according to the sorting option user chose, and finally exits out of the program.

Test Status: Passed
### Test 7: Modify account
1. User launches program.
2. User enters hostname (localhost)
3. User selects the type of user they are.
4. User logs in using their username and password.
5. User selects “Modify account.”
6. User selects to edit password then enters new password.
7. User returns to main page and exits.
8. User repeats steps 1-4, using new password to log in.
9. User selects “Modify account.”, selects to edit username then enters new username.
10.User returns to main page and exits.
11. User repeats steps 1-4, using new username to log in.
12. User selects “Modify account.”, selects to delete account and confirms.
13.User repeats steps 1-4 to log in.

Expected Result: User is able to change their password and log in with their new password after step 8; User is able to change their username and log in with their new username after step 11; User is able to delete account and can’t log in after step 13. System should give an error indicating there’s no such user.

Test Status: Passed

### Test 8: Seller functions
1. User launches program
2. User enters hostname (localhost)
3. User selects seller as type of user
4. User logs in using their username and password
5. User selects create a store and enters the store name
6. User selects modify products and selects the newly created store
7. User selects add a product and selects create product in gui
8. User enters product name, description, quantity, and price
9. User exits program

Expected Result: Program connects to server, logs into the account, creates a store, 
creates a product under the store, and finally exits out of the program.

Test Status: Passed
