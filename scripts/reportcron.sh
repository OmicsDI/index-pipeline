6,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57,01,04 * * * * bash cronetask/oplog

7,10,13,16,19,22,25,28,31,34,37,40,43,46,49,52,55,58,02 * * * * bash /home/gaurhari/cronetask/connnector

#0 0 * * 5 bash /home/gaurhari/openclosedjobs.sh

*/5 * * * * bash /home/gaurhari/amreports.sh

*/5 * * * * bash /home/gaurhari/recruiterreports.sh
1 */1 * * * /home/gaurhari/killprocess.sh

