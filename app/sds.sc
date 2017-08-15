import org.mindrot.jbcrypt.BCrypt

BCrypt.hashpw("qwerty123",BCrypt.gensalt())


BCrypt.checkpw("qwerty123","$2a$10$OheicC7rvlhpRSUDS12G8.FLaCe.AEQkZT/8ld/asUQlUlCDYISPG")
