INSERT INTO BRAND (ID, NAME)
VALUES (1, 'Stihl'),
       (2, 'Werner'),
       (3, 'DeWalt'),
       (4, 'Ridgid');

INSERT INTO TOOL_TYPE (ID, NAME, DAILY_CHARGE, WEEKDAY_CHARGE, WEEKEND_CHARGE, HOLIDAY_CHARGE)
VALUES (1, 'Ladder', 1.99, 1, 1, 0),
       (2, 'Chainsaw', 1.49, 1, 0, 1),
       (3, 'Jackhammer', 2.99, 1, 0, 0);

INSERT INTO TOOLS (ID, CODE, TOOL_TYPE_ID, BRAND_ID)
VALUES (1, 'CHNS', 2, 1),
       (2, 'LADW', 1, 2),
       (3, 'JAKD', 3, 3),
       (4, 'JAKR', 3, 4);