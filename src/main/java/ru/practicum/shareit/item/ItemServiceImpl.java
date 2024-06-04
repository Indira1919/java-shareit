package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public List<ItemDtoComments> getItemsOfUser(Integer userId, Integer from, Integer page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        List<ItemDtoComments> itemsDtoComments = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, page))
                .stream()
                .map(ItemMapper::toItemDtoComments)
                .collect(Collectors.toList());

        for (ItemDtoComments itemDtoComments : itemsDtoComments) {
            itemDtoComments.setComments(commentRepository.findAllByItemId(itemDtoComments.getId())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));

            bookingRepository
                    .findFirstByItemIdAndStartLessThanEqualAndStatus(itemDtoComments.getId(), now(), Status.APPROVED,
                            Sort.by(Sort.Direction.DESC, "start"))
                    .ifPresent(value -> itemDtoComments
                            .setLastBooking(new ItemDtoComments.BookingDto(value.getId(), value.getBooker().getId())));

            bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusEquals(itemDtoComments.getId(), now(), Status.APPROVED,
                            Sort.by(Sort.Direction.ASC, "start"))
                    .ifPresent(value -> itemDtoComments
                            .setNextBooking(new ItemDtoComments.BookingDto(value.getId(), value.getBooker().getId())));
        }

        return itemsDtoComments;
    }

    @Override
    public ItemDtoComments getItemById(Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена"));

        ItemDtoComments itemDtoComments = ItemMapper.toItemDtoComments(item);
        itemDtoComments.setComments(commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        if (item.getOwner().getId().equals(userId)) {
            bookingRepository
                    .findFirstByItemIdAndStartLessThanEqualAndStatus(itemDtoComments.getId(), now(), Status.APPROVED,
                            Sort.by(Sort.Direction.DESC, "start"))
                    .ifPresent(value -> itemDtoComments
                            .setLastBooking(new ItemDtoComments.BookingDto(value.getId(), value.getBooker().getId())));

            bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusEquals(itemDtoComments.getId(), now(), Status.APPROVED,
                            Sort.by(Sort.Direction.ASC, "start"))
                    .ifPresent(value -> itemDtoComments
                            .setNextBooking(new ItemDtoComments.BookingDto(value.getId(), value.getBooker().getId())));
        }
        return itemDtoComments;
    }

    @Override
    public List<ItemDto> getItems(String description, Integer from, Integer size) {
        List<Item> items = new ArrayList<>();

        if (description != null && !description.isBlank()) {
            items = itemRepository
                    .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(description,
                            description, true, PageRequest.of(from, size));
        }

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Integer id, Integer userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Невозможно обновить данные");
        }

        itemRepository.save(ItemMapper.toItemUpdate(itemDto, item));

        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        Item item = ItemMapper.toItem(itemDto);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ObjectNotFoundException("Запрос не найден"));

            item.setRequest(itemRequest);
        }

        item.setOwner(user);
        itemRepository.save(item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer itemId, Integer userId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена"));

        List<Booking> bookingUser = bookingRepository.findAllByBookerId(user.getId(),
                Sort.by(Sort.Direction.DESC, "start"));

        if (!bookingUser.isEmpty()) {
            for (Booking booking : bookingUser) {
                if (booking.getItem().getId().equals(itemId) && booking.getStatus().equals(Status.APPROVED)
                        && booking.getEnd().isBefore(now())) {
                    Comment comment = CommentMapper.toComment(commentDto);
                    comment.setItem(item);
                    comment.setAuthor(user);
                    comment.setCreated(now());
                    commentRepository.save(comment);
                    return CommentMapper.toCommentDto(comment);
                }
            }
        }

        throw new BadRequestException("Вы не можете оставить комментарий");
    }

    @Override
    @Transactional
    public void deleteItem(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена"));

        itemRepository.deleteById(itemId);
    }
}
