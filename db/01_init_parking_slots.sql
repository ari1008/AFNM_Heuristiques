BEGIN;

INSERT INTO parking_slots ("row", number, code, has_charger) VALUES
('A', 1 , 'A01', true ), ('A', 2 , 'A02', true ), ('A', 3 , 'A03', true ),
('A', 4 , 'A04', true ), ('A', 5 , 'A05', true ), ('A', 6 , 'A06', true ),
('A', 7 , 'A07', true ), ('A', 8 , 'A08', true ), ('A', 9 , 'A09', true ),
('A', 10, 'A10', true ),
('B', 1 , 'B01', false), ('B', 2 , 'B02', false), ('B', 3 , 'B03', false),
('B', 4 , 'B04', false), ('B', 5 , 'B05', false), ('B', 6 , 'B06', false),
('B', 7 , 'B07', false), ('B', 8 , 'B08', false), ('B', 9 , 'B09', false),
('B', 10, 'B10', false),
('C', 1 , 'C01', false), ('C', 2 , 'C02', false), ('C', 3 , 'C03', false),
('C', 4 , 'C04', false), ('C', 5 , 'C05', false), ('C', 6 , 'C06', false),
('C', 7 , 'C07', false), ('C', 8 , 'C08', false), ('C', 9 , 'C09', false),
('C', 10, 'C10', false),
('D', 1 , 'D01', false), ('D', 2 , 'D02', false), ('D', 3 , 'D03', false),
('D', 4 , 'D04', false), ('D', 5 , 'D05', false), ('D', 6 , 'D06', false),
('D', 7 , 'D07', false), ('D', 8 , 'D08', false), ('D', 9 , 'D09', false),
('D', 10, 'D10', false),
('E', 1 , 'E01', false), ('E', 2 , 'E02', false), ('E', 3 , 'E03', false),
('E', 4 , 'E04', false), ('E', 5 , 'E05', false), ('E', 6 , 'E06', false),
('E', 7 , 'E07', false), ('E', 8 , 'E08', false), ('E', 9 , 'E09', false),
('E', 10, 'E10', false),
('F', 1 , 'F01', true ), ('F', 2 , 'F02', true ), ('F', 3 , 'F03', true ),
('F', 4 , 'F04', true ), ('F', 5 , 'F05', true ), ('F', 6 , 'F06', true ),
('F', 7 , 'F07', true ), ('F', 8 , 'F08', true ), ('F', 9 , 'F09', true ),
('F', 10, 'F10', true );

COMMIT;
