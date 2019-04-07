package ru.immmus.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.immmus.AbstractMessageService;
import ru.immmus.profiles.Common;

@ActiveProfiles(Common.profile)
@SpringBootTest(classes = MessageServiceImpl.class)
class MessageServiceImplTest extends AbstractMessageService { }