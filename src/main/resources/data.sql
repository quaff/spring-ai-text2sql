insert into grade(id, name) values(1, '一年级');
insert into grade(id, name) values(2, '二年级');

insert into class(id, name, grade_id) values(1, '一年级一班', 1);
insert into class(id, name, grade_id) values(2, '一年级二班', 1);
insert into class(id, name, grade_id) values(3, '二年级一班', 2);
insert into class(id, name, grade_id) values(4, '二年级二班', 2);

insert into exam_scores(name, score, class_id) values('张三', 78, 1);
insert into exam_scores(name, score, class_id) values('李四', 85, 1);
insert into exam_scores(name, score, class_id) values('王五', 90, 1);
insert into exam_scores(name, score, class_id) values('刘七', 79, 2);
insert into exam_scores(name, score, class_id) values('孙八', 88, 2);
insert into exam_scores(name, score, class_id) values('赵九', 94, 2);
insert into exam_scores(name, score, class_id) values('周一', 50, 3);
insert into exam_scores(name, score, class_id) values('谭九', 98, 3);
insert into exam_scores(name, score, class_id) values('杨十', 64, 3);
insert into exam_scores(name, score, class_id) values('牛大', 58, 4);
insert into exam_scores(name, score, class_id) values('马武', 92, 4);
insert into exam_scores(name, score, class_id) values('胡二', 65, 4);