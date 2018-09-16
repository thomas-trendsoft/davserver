# davserver

This is my first real open source project. It is a try to build a java implementation of WebDAV (RFC4918) with additional support for CalDAV and CardDAV. 

It is an learning project to get a protocol implementation. The target is still to get a production ready WebDAV Protocol implementation. The API will be able to make simple implementations of file or database resources behind a WebDAV server interface. 

The second step with CalDAV and CardDAV should enable the server to be able to serve resources, calendars, contacts and todos. There will be no user interface or anything else, the project aims only to be the protocol implementation. 

Authentication will be target in the future. 
 
THE PROJECT IS WORK IN PROGRESS and no useful release is done yet.

For testing surpose i will try to get this framework running against the litmus project (https://github.com/tolsen/litmus). First steps are done the library passes the base tests. 

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
-> running 'copymove':
 0. init.................. pass
 1. begin................. pass
 2. copy_init............. pass
 3. copy_simple........... pass
 4. copy_overwrite........ FAIL (COPY overwrites collection: 403 Forbidden)
 5. copy_nodestcoll....... WARNING: COPY to non-existant collection '/simple/litmus/nonesuch' gave '403 Forbidden' not 409
    ...................... pass (with 1 warning)
 6. copy_cleanup.......... pass
 7. copy_content_check.... pass
 8. copy_coll_depth....... FAIL (COPY destination missing coll /simple/litmus/cdest/subsrc? 404 Not Found)
 9. copy_coll............. WARNING: COPY to new collection gave 200, should be 201
    ...................... FAIL (COPY-to-self should fail)
10. depth_zero_copy....... FAIL (collection COPY é/simple/litmus/copy-a' to é/simple/litmus/copy-b', depth infinity: 500 Internal Server Error)
11. copy_med_on_coll...... FAIL (collection COPY é/simple/litmus/foofile' to é/simple/litmus/dest': 403 Forbidden)
12. move.................. pass
13. move_coll............. WARNING: COPY to new collection gave 200, should be 201
WARNING: Move to new collection gave 200, should be 201
    ...................... FAIL (MOVE-to-self should fail)
14. move_cleanup.......... pass
15. move_content_check.... pass
16. move_collection_check. FAIL (DELETE on normal resource failed: 404 Not Found)
17. finish................ pass
<- summary for écopymove': of 18 tests run: 11 passed, 7 failed. 61.1%
-> 4 warnings were issued.

