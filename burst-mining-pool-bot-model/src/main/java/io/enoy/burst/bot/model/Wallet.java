package io.enoy.burst.bot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public final class Wallet {

	@Id
	private String id;

}
