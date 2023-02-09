# Metronome Take-home Code Screen

### Instruction

1. `git clone` this repo to your local environment.
2. Open the project with IDE (e.g. IntelliJ), preferably with Java 17 SDK running.
![Screenshot 2023-02-09 at 12.19.34 PM.png](resources%2FScreenshot%202023-02-09%20at%2012.19.34%20PM.png)
3. Simply run Application.java in src/eventparser in your IDE, you should see the pop-up Run window like the screenshot below:
![Screenshot 2023-02-09 at 12.22.30 PM.png](resources%2FScreenshot%202023-02-09%20at%2012.22.30%20PM.png)
4. Type the input (start time & end time in the format yyyy-MM-dd HH:mm:ss) and get the count for each hour window
![Screenshot 2023-02-09 at 12.28.22 PM.png](resources%2FScreenshot%202023-02-09%20at%2012.28.22%20PM.png)

### Original Description

Attached, find the file `events.csv`, which contains a log of events with the
the format customer\_id, event\_type, transaction\_id, timestamp.

Your task is to write a program that answers the following question:

> How many events did customer X send in the one hour buckets between timestamps A and B.

So, for example, let's say you have the following usage events for a single customer:

- 2022-03-01T03:01:00Z event_1
- 2022-03-01T04:29:00Z event_2
- 2022-03-01T04:15:00Z event_3
- 2022-03-01T05:08:00Z event_4

If you sent start and end timestamps of Mar 1 3:00 am - Mar 1 6:00 am, we’d expect to see these output values (format is up to you):
- 2022-03-01T03:00:00Z bucket -> 1
- 2022-03-01T04:00:00Z bucket -> 2
- 2022-03-01T05:00:00Z bucket -> 1

Choice of language, platform, and libraries is left up to you, as long as the
person evaluating your submission doesn't have to think too hard to figure out
how to run it. We all use recent macOS.

We expect this exercise to take 1-3 hours.

*Bonus:* Include an HTTP service that answers the same question.
