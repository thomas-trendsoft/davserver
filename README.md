# davserver

This is my first real open source project. It is a try to build a java implementation of WebDAV (RFC4918) with additional support for CalDAV and CardDAV. 

It is an learning project to get a protocol implementation. The target is still to get a production ready WebDAV Protocol implementation. The API will be able to make simple implementations of file or database resources behind a WebDAV server interface. 

The second step with CalDAV and CardDAV should enable the server to be able to serve resources, calendars, contacts and todos. There will be no user interface or anything else, the project aims only to be the protocol implementation. 

Authentication will be target in the future. 
 
THE PROJECT IS WORK IN PROGRESS and no useful release is done yet.

For testing surpose i will try to get this framework running against the litmus project (https://github.com/tolsen/litmus).

## Litmus Progress

-> running `basic':
 0. init.................. pass
 1. begin................. pass
 2. options............... pass
 3. put_get............... pass
 4. put_get_utf8_segment.. pass
 5. mkcol_over_plain...... pass
 6. delete................ pass
 7. delete_null........... pass
 8. delete_fragment....... WARNING: DELETE removed collection resource with Request-URI including fragment; unsafe
    ...................... pass (with 1 warning)
 9. mkcol................. pass
10. mkcol_percent_encoded. pass
11. mkcol_again........... pass
12. delete_coll........... pass
13. mkcol_no_parent....... pass
14. mkcol_with_body....... pass
15. mkcol_forbidden....... pass
16. chk_ETag.............. pass
17. finish................ pass
<- summary for `basic': of 18 tests run: 18 passed, 0 failed. 100.0%
