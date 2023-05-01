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
7. User returns to main page and exits

Expected Result: Program connects to server, logs into the account, 
Goes to the product page and purchases the amount requested, and finally exits out of the program.

Test Status: Passed
### Test 3: Seller functions
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