CREATE TABLE IF NOT EXISTS Comment (
    id INT NOT NULL,
    post_id INT NOT NULL,
    name varchar(255) NOT NULL,
    email varchar(100) NOT NULL,
    body text NOT NULL,
    version int,
    PRIMARY KEY (id)
);