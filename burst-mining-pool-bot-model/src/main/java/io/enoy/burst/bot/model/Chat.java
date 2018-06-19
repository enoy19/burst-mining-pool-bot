package io.enoy.burst.bot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public final class Chat {

	@Id
	private String id;

}
