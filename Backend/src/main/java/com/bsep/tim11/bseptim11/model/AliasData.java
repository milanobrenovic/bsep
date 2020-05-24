package com.bsep.tim11.bseptim11.model;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;



import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;



@Entity
public class AliasData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name= "alias")
	private String alias;
	@OneToMany(mappedBy = "aliasData", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<AliasData> aliases = new HashSet<AliasData>();
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private AliasData aliasData;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Set<AliasData> getAliases() {
		return aliases;
	}
	public void setAliases(Set<AliasData> aliases) {
		this.aliases = aliases;
	}
	public AliasData(){
		
	}
	
	public AliasData(Long id, String alias, Set<AliasData> aliases) {
		super();
		this.id = id;
		this.alias = alias;
		this.aliases = aliases;
	}
	public AliasData(Long id, String alias) {
		super();
		this.id = id;
		this.alias = alias;
	}
	public AliasData(String alias) {
		super();
		this.alias = alias;

	}
	public AliasData getAliasData() {
		return aliasData;
	}
	public void setAliasData(AliasData aliasData) {
		this.aliasData = aliasData;
	}
	
	
	
	
	
	
	
}
