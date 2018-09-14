# davserver

This is my first real open source project. It is a try to build a java implementation of WebDAV (RFC4918) with additional support for CalDAV and CardDAV. 

It is an learning project to get a protocol implementation.
 
THE PROJECT IS WORK IN PROGRESS and no useful release is done yet.

For testing surpose i will try to get this framework running against the litmus project (https://github.com/tolsen/litmus).

## Litmus Progress

0. init.................. pass
1. begin................. pass
2. options............... pass
3. put_get............... pass
4. put_get_utf8_segment.. pass
5. mkcol_over_plain...... pass
6. delete................ pass
7. delete_null........... pass
8. delete_fragment....... FAIL (DELETE on `/simple/litmus/frag/': 404 Not Found)
