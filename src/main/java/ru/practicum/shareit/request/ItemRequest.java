package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

/**
 * TODO Sprint add-item-requests.
 */
//@Entity
//@Table(name = "requests")
@Getter
@Setter
public class ItemRequest {
  //  @Id
  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
  //  @ManyToOne(fetch = FetchType.LAZY)
    private User requestor;

    //private LocalDateTime created;
}
