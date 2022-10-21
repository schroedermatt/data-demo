package org.msse.demo.mockdata.music.ticket;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

@Service
public class TicketFaker extends BaseFaker {

  public TicketFaker(Faker faker) {
    super(faker);
  }

  public Ticket generate(String customerId, String eventId) {
    return generate(randomId(), customerId, eventId);
  }

  public Ticket generate(String ticketId, String customerId, String eventId) {
    return new Ticket(
            ticketId,
            customerId,
            eventId,
            (double)faker.number().numberBetween(1, 2500)
    );
  }
}
