package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NamedEntityGraph(name = "comment_entity-graph", attributeNodes = @NamedAttributeNode("author"))
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @OneToOne(fetch = FetchType.LAZY)
    private User author;
    private LocalDateTime created;
}
