define ["../../core/EventService.js"], (events) ->
  describe "EventService", () ->
    it "should notify listeners", () ->
      notificationData = null

      events.listen "testnamespace", "testname", (data) -> notificationData = data
      events.emit "testnamespace", "testname", 123
      expect(notificationData).toBe(123)

    it "should not notify listeners to other events", () ->
      notificationData = null

      events.listen "testnamespace", "testname2", (data) -> notificationData = data
      events.emit "testnamespace", "testname1", 123
      expect(notificationData).toBe(null)

    it "should not notify unsubscribed listeners anymore", () ->
      notificationData = null

      listenerId = events.listen "testnamespace", "testname", (data) -> notificationData = data
      events.emit "testnamespace", "testname", 123
      expect(notificationData).toBe(123)

      events.unlisten listenerId
      events.emit "testnamespace", "testname", 321
      expect(notificationData).toBe(123)
