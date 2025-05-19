DROP TABLE IF EXISTS exam_scores;
DROP TABLE IF EXISTS class;
DROP TABLE IF EXISTS grade;

CREATE TABLE grade (
    id INTEGER,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE class (
    id INTEGER,
    name VARCHAR(100) NOT NULL,
    grade_id INTEGER,
    PRIMARY KEY (id),
    CONSTRAINT fk_gradle_id FOREIGN KEY (grade_id) REFERENCES grade(id)
);

CREATE TABLE exam_scores (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    score INTEGER NOT NULL,
    class_id INTEGER,
    PRIMARY KEY (id),
    CONSTRAINT fk_class_id FOREIGN KEY (class_id) REFERENCES class(id)
);
