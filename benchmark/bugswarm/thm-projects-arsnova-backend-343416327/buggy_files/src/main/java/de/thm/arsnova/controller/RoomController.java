package de.thm.arsnova.controller;

import de.thm.arsnova.entities.Room;
import de.thm.arsnova.services.RoomService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room")
public class RoomController extends AbstractEntityController<Room> {
	private RoomService roomService;

	public RoomController(final RoomService roomService) {
		super(roomService);
		this.roomService = roomService;
	}

	@Override
	@RequestMapping(GET_MAPPING)
	public Room get(@PathVariable final String id) {
		return super.get(id);
	}
}
