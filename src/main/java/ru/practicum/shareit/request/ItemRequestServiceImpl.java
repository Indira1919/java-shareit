package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<ItemRequestDto> getItemRequestUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        List<ItemRequestDto> itemRequestsDto = itemRequestRepository.findAllByRequestor(user,
                        Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        for (ItemRequestDto itemRequestDto : itemRequestsDto) {
            itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                    .stream()
                    .map(ItemMapper::toItemDtoRequest)
                    .collect(Collectors.toList()));
        }

        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequest(Integer from, Integer size, Integer userId) {
        //сортировка по дате
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        List<ItemRequestDto> itemRequestsDto = itemRequestRepository.findAllByRequestorNot(user,
                PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        for (ItemRequestDto itemRequestDto : itemRequestsDto) {
            itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                    .stream()
                    .map(ItemMapper::toItemDtoRequest)
                    .collect(Collectors.toList()));
        }

        return itemRequestsDto;
    }

    @Override
    public ItemRequestDto getItemRequestById(Integer requestId, Integer userId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос не найден"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDtoRequest)
                .collect(Collectors.toList()));

        return itemRequestDto;
    }

    @Transactional
    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new BadRequestException("Описание не может быть пустым");
        }

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }
}
