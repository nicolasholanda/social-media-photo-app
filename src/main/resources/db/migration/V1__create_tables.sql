CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE photos (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    caption      VARCHAR(200),
    image_data   BLOB         NOT NULL,
    content_type VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id    BIGINT       NOT NULL,
    CONSTRAINT fk_photo_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE tags (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE photo_tags (
    photo_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (photo_id, tag_id),
    CONSTRAINT fk_pt_photo FOREIGN KEY (photo_id) REFERENCES photos (id),
    CONSTRAINT fk_pt_tag   FOREIGN KEY (tag_id)   REFERENCES tags (id)
);

CREATE TABLE comments (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    content    VARCHAR(500) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id  BIGINT       NOT NULL,
    photo_id   BIGINT       NOT NULL,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_comment_photo  FOREIGN KEY (photo_id)  REFERENCES photos (id)
);
