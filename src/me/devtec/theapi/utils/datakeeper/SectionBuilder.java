package me.devtec.theapi.utils.datakeeper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.devtec.theapi.utils.json.Json;

public class SectionBuilder {
	
	public class SectionHolder {
		public SectionHolder(String d) {
			name=d;
		}
		
		public SectionHolder find(String name) {
			if(holders!=null)
			for(SectionHolder a : holders)
				if(a.name.equals(name))
					return a;
			return null;
		}
		
		public SectionHolder create(String name) {
			SectionHolder sec = new SectionHolder(name);
			sec.space=space+' '+' ';
			if(holders==null)holders=new LinkedList<>();
			holders.add(sec);
			return sec;
		}
		
		//PREPARING PART
		List<SectionHolder> holders;
		String name;
		Object[] val;
		String space;
	}
	
	Map<String, SectionHolder> secs = new LinkedHashMap<>();
	
	public SectionBuilder(List<String> keys, Map<String, Object[]> map) {
		for(String d : keys) {
			SectionHolder sec = new SectionHolder(d);
			sec.space="";
			secs.put(d, sec);
		}
		for(Entry<String, Object[]> e : map.entrySet()) {
			if(e.getKey().indexOf('.') > -1) {
				String[] split = split(e.getKey());
				SectionHolder holder = secs.get(split[0]);
				//DEEP FIND SECTION
				for(int i = 1; i < split.length; ++i) {
					SectionHolder f = holder.find(split[i]);
					if(f==null)
						f=holder.create(split[i]);
					holder=f;
				}
				//SET VALUE
				holder.val=e.getValue();
			}else {
				secs.get(e.getKey()).val=e.getValue();
			}
		}
	}

	private static String[] split(String text) {
        int off = 0, next = 0;
        ArrayList<String> list = new ArrayList<>();
        while ((next = text.indexOf('.', off)) != -1) {
            list.add(text.substring(off, next));
            off = next + 1;
        }
        if (off == 0)
            return new String[] {text};
        list.add(text.substring(off, text.length()));
        return list.toArray(new String[list.size()]);
	}

	@SuppressWarnings("unchecked")
	public synchronized void start(SectionHolder section, StringBuilder b) {
		StringBuilder bab = new StringBuilder(section.name.length()+section.space.length()+2);
		bab.append(section.space);
		String split = section.name.substring(0, section.name.length()-1);
		if(split.length()==1) { //char
			bab.append('"').append(split).append('"').append(':');
		}else
		if(split.contains(":")) {
			bab.append('"').append(split).append('"').append(':');
		}else
		if(split.startsWith("#")) { //starts with comment
			bab.append('"').append(split).append('"').append(':');
		}else bab.append(section.name).append(':');
		try {
		Object[] aw = section.val;
		if(aw==null) {
			b.append(bab).append(System.lineSeparator());
			for (SectionHolder d : section.holders)
				start(d, b);
			return;
		}
		Collection<String> list = (Collection<String>)aw[1];
		Object o = aw[0];
		if(list != null)
			for (String s : list)
				b.append(section.space).append(s).append(System.lineSeparator());
		if(o==null)b.append(bab).append(System.lineSeparator());
		else {
			if (o instanceof Collection || o instanceof Object[]) {
				String splitted = section.space+'-'+' ';
				if (o instanceof Collection) {
					if(!((Collection<?>) o).isEmpty()) {
						try {
							if(aw[3]!=null && (int)aw[3]==1) {
								addQuotes(b,bab,(String)aw[2], o instanceof Comparable && !(o instanceof String));
							}else {
								b.append(bab).append(System.lineSeparator());
								for (Object a : (Collection<?>) o) {
									if(a instanceof String)
										addQuotesSplit(b,splitted,(String)a);
									else
										addQuotesSplit(b,splitted,a);
								}
							}
						}catch(Exception er) {
							b.append(bab).append(System.lineSeparator());
							for (Object a : (Collection<?>) o) {
								if(a instanceof String)
									addQuotesSplit(b,splitted,(String)a);
								else
									addQuotesSplit(b,splitted,a);
							}
					}}else
						b.append(bab).append(' ').append('[').append(']').append(System.lineSeparator());
				} else {
					if(((Object[]) o).length!=0) {
						try {
							if(aw[3]!=null && (int)aw[3]==1) {
								addQuotes(b,bab,(String)aw[2], o instanceof Comparable && !(o instanceof String));
							}else {
								b.append(bab).append(System.lineSeparator());
								for (Object a : (Object[]) o) {
									if(a instanceof String)
										addQuotesSplit(b,splitted,(String)a);
									else
										addQuotesSplit(b,splitted,a);
								}
							}
						}catch(Exception er) {
							b.append(bab).append(System.lineSeparator());
							for (Object a : (Object[]) o) {
								if(a instanceof String)
									addQuotesSplit(b,splitted,(String)a);
								else
									addQuotesSplit(b,splitted,a);
							}
					}}else
						b.append(bab).append(' ').append('[').append(']').append(System.lineSeparator());
				}
			} else {
				try {
					if(aw[3]!=null && (int)aw[3]==1) {
						addQuotes(b,bab,(String)aw[2], o instanceof Comparable && !(o instanceof String));
					}else {
						if(o instanceof String)
							addQuotes(b,bab,(String)o, false);
						else
							addQuotes(b,bab,o);
					}
				}catch(Exception er) {
					if(o instanceof String)
						addQuotes(b,bab,(String)o, false);
					else
						addQuotes(b,bab,o);
				}
			}
		}
		for (SectionHolder d : section.holders)
			start(d, b);
		}catch(Exception err) {}
	}

	protected synchronized void addQuotesSplit(StringBuilder b, CharSequence split, String aw) {
		b.append(split);
		b.append('"');
		b.append(aw);
		b.append('"');
		b.append(System.lineSeparator());
	}
	
	protected synchronized void addQuotesSplit(StringBuilder b, CharSequence split, Object aw) {
		b.append(split);
		b.append(Json.writer().write(aw));
		b.append(System.lineSeparator());
	}
	
	protected synchronized void addQuotes(StringBuilder b, CharSequence pathName, String aw, boolean add) {
		b.append(pathName).append(' ');
		if(add) {
			b.append(aw);
		}else {
			b.append('"');
			b.append(aw);
			b.append('"');
		}
		b.append(System.lineSeparator());
	}
	
	protected synchronized void addQuotes(StringBuilder b, CharSequence pathName, Object aw) {
		b.append(pathName).append(' ');
		b.append(Json.writer().write(aw));
		b.append(System.lineSeparator());
	}

	public void write(StringBuilder d) {
		for(Entry<String, SectionHolder> c : secs.entrySet())
			start(c.getValue(),d);
	}
}
