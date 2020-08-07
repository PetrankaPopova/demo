package diplomna.service.serviceImpl;

import diplomna.error.exception.EventNameAlreadyExistsException;
import diplomna.error.exception.EventNotFoundException;
import diplomna.model.entity.Event;
import diplomna.model.entity.User;
import diplomna.model.service.EventAddServiceModel;
import diplomna.model.service.LogServiceModel;
import diplomna.repository.EventRepository;
import diplomna.service.EventService;
import diplomna.service.LogService;
import diplomna.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private ModelMapper modelMapper;
    private EventRepository eventRepository;
    private UserService userService;
    private LogService logService;

    @Autowired
    public EventServiceImpl(ModelMapper modelMapper, EventRepository eventRepository, UserService userService, LogService logService) {
        this.modelMapper = modelMapper;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.logService = logService;
    }


    @Override
    public EventAddServiceModel createEvent(EventAddServiceModel serviceModel, String username) {
        Event event = this.eventRepository
                .findByName(serviceModel.getName())
                .orElse(null);

        if (event != null) {
            throw new EventNameAlreadyExistsException("Event already exists");
        }

        event = this.modelMapper.map(serviceModel, Event.class);
        event.setUser(this.modelMapper.map(this.userService.findByUsername(username), User.class));

        LogServiceModel logServiceModel = new LogServiceModel();
        logServiceModel.setUsername(username);
        logServiceModel.setDescription(serviceModel.getName() +" - Event created");
        logServiceModel.setTime(LocalDateTime.now());

        this.logService.seedLogInDB(logServiceModel);

        this.eventRepository.saveAndFlush(event);

        return this.modelMapper.map(event, EventAddServiceModel.class);
    }

    @Override
    public List<EventAddServiceModel> findAllEvents() {
        return this.eventRepository.findAll()
                .stream()
                .map(event -> this.modelMapper.map(event, EventAddServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventAddServiceModel> findEventByUsername(String name) {
        return this.eventRepository.findAllByUser_Username(name)
                .stream()
                .map(event -> modelMapper.map(event, EventAddServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public EventAddServiceModel findById(String id) {
        return this.eventRepository
                .findById(id)
                .map(event -> this.modelMapper.map(event, EventAddServiceModel.class))
                .orElseThrow(() -> new EventNotFoundException("Event with the given id was not found!"));
    }

    @Override
    public EventAddServiceModel editEvent(String id, EventAddServiceModel eventServiceModel) {
        Event event = this.eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with the given id was not found!"));
        event.setName(eventServiceModel.getName());
        event.setDescription(eventServiceModel.getDescription());
        event.setDate(eventServiceModel.getDate());

        LogServiceModel logServiceModel = new LogServiceModel();
        logServiceModel.setUsername(event.getUser().getUsername());
        logServiceModel.setDescription(event.getName() +" - Event update");
        logServiceModel.setTime(LocalDateTime.now());

        this.logService.seedLogInDB(logServiceModel);

        return this.modelMapper.map(this.eventRepository.saveAndFlush(event), EventAddServiceModel.class);
    }

    @Override
    public void deleteEvent(String id) {
        Event event = this.eventRepository
                .findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with the given id was not found!"));


        LogServiceModel logServiceModel = new LogServiceModel();
        logServiceModel.setUsername(event.getUser().getUsername());
        logServiceModel.setDescription(event.getName() +" - Event delete");
        logServiceModel.setTime(LocalDateTime.now());

        this.logService.seedLogInDB(logServiceModel);

        this.eventRepository.delete(event);
    }

   // @Scheduled(fixedRate = 5000000)
  //  private void deleteEventIfDateIsOld() {
    //    List<Event> events = new ArrayList<>(this.eventRepository.findAll());
    //    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    //  LocalDateTime nowDate = LocalDateTime.parse(now);

       // for (Event event : events) {
         //   LocalDateTime eventDate = LocalDateTime.parse(event.getDate());
          //  if (eventDate.isBefore(nowDate)) {
          //      this.eventRepository.delete(event);
           // }
       // }
   // }
}
