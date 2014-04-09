package br.cad.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataSistema {
	private static final SimpleDateFormat dataPattern = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat dataTimePattern = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	@SuppressWarnings("deprecation")
	static public boolean compareDDMMYYYY(Date d1, Date d2) {
		if (d1 == null || d2 == null)
			return false;

		if (d1.getDay() != d2.getDay())
			return false;
		if (d1.getMonth() != d2.getMonth())
			return false;
		if (d1.getYear() != d2.getYear())
			return false;
		return true;
	}

	static public Date getDate(String dateDDMMYYYY) {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(dateDDMMYYYY);
		} catch (Exception e) {
			return null;
		}
	}

	static public Date getInfiniteDate() {
		return getDate("12/31/3000");
	}

	static public String getDataTimeCorrenteDDMMYYYYHHMM() {
		return dataTimePattern.format(new Date());
	}

	static public String getDataCorrenteDDMMYYYY() {
		Date dt = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		return sd.format(dt);
	}

	static public String formataDataDDMMYY(Date dt) {
		SimpleDateFormat sd = new SimpleDateFormat("ddMMyy");
		return sd.format(dt);
	}

	/**
	 * Formata a data com o pattern dd/MM/yyyy
	 * 
	 * @param dt
	 *            data a ser formatada
	 * @return Uma string com a data formatada
	 */
	static public String formataData(Date dt) {
		return dataPattern.format(dt);
	}

	static public String getDataCorrenteExtenso() {
		Date dt = new Date();
		DateFormat dtSaida = DateFormat.getDateInstance(DateFormat.LONG);
		return dtSaida.format(dt);
	}

	static public Timestamp getDataCorrenteTimestamp() {
		Timestamp tm = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return tm;
	}

	static public Date getDataCorrenteDate() {
		Date d = new Date(Calendar.getInstance().getTimeInMillis());
		return d;
	}

	static public Timestamp getDataDDMMYYYtoTimestamp(String dataDDMMYYYY) {
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		Date dt = new Date();
		try {
			dt = sd.parse(dataDDMMYYYY);
		} catch (ParseException ex) {
			System.out.println("Erro na conversao de data sera retornada a data corrente!");
		}
		Timestamp tm = new Timestamp(dt.getTime());
		return tm;
	}

	static public Timestamp getDatatoTimestamp(Date data) {
		Timestamp tm;
		try {
			tm = new Timestamp(data.getTime());
		} catch (Exception e) {
			tm = null;
		}
		return tm;
	}

	static public Date getTimestamptoDate(Timestamp dataHora) {
		Date dt;
		try {
			dt = new Date(dataHora.getTime());
		} catch (Exception e) {
			dt = null;
		}
		return dt;
	}

	static public int getDateYear() {
		Date dt = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		String datas = sd.format(dt);

		return Integer.parseInt(datas.substring(6, 10));
	}

	static public int getDateMonth() {
		Date dt = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		String datas = sd.format(dt);

		return Integer.parseInt(datas.substring(3, 5));
	}

	static public int getDateDay() {
		Date dt = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		String datas = sd.format(dt);

		return Integer.parseInt(datas.substring(0, 2));
	}

	static public String parseTimestampString(Timestamp tm) {
		Date dt = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		dt = (Date) tm.clone();
		return sd.format(dt);
	}

	static public String parseTimeStamptoString(Timestamp tm) {
		Date dt = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		dt = (Date) tm.clone();
		return sd.format(dt);
	}

	static public Timestamp parseStringTimestamp(String dataddMMyyyyHHmmss) {
		// "dd/MM/yyyy HH:mm:ss"
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date dt = new Date();
		try {
			dt = sd.parse(dataddMMyyyyHHmmss);
		} catch (ParseException ex) {
			System.out.println("Erro na conversao de data sera retornada a data corrente!");
		}
		Timestamp tm = new Timestamp(dt.getTime());
		return tm;
	}

	static public String parseDateString(Date data) {
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		return sd.format(data);
	}

	static public Date parseStringToDate(String data) {
		try {
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
			return sd.parse(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static public Timestamp addMinute(Timestamp tm, int minute) {
		Calendar c = Calendar.getInstance();
		c.setTime(tm);
		c.add(Calendar.MINUTE, minute);
		return new Timestamp(c.getTimeInMillis());
	}

	static public Timestamp addHora(Timestamp tm, int hora) {
		Calendar c = Calendar.getInstance();
		c.setTime(tm);
		c.add(Calendar.HOUR, hora);
		return new Timestamp(c.getTimeInMillis());
	}

	static public Date addMes(Date tm, int mes) {
		Calendar c = Calendar.getInstance();
		c.setTime(tm);
		c.add(Calendar.MONTH, mes);
		return new Date(c.getTimeInMillis());
	}

	static public Date addDia(Date tm, int dia) {
		Calendar c = Calendar.getInstance();
		c.setTime(tm);
		c.add(Calendar.DAY_OF_MONTH, dia);
		return new Date(c.getTimeInMillis());
	}

	static public Date addAno(Date tm, int ano) {
		Calendar c = Calendar.getInstance();
		c.setTime(tm);
		c.add(Calendar.YEAR, ano);
		return new Date(c.getTimeInMillis());
	}

	public static String formatDate(Date date, String format) {
		try {
			SimpleDateFormat sd = new SimpleDateFormat(format);
			return sd.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Converte uma String para um objeto Date. Caso a String seja vazia ou nula, retorna null - para facilitar em casos onde formularios podem ter campos de datas vazios.
	 * 
	 * @param data
	 *            String no formato dd/MM/yyyy a ser formatada
	 * @return Date Objeto Date ou null caso receba uma String vazia ou nula
	 * @throws Exception
	 *             Caso a String esteja no formato errado
	 */
	public static java.util.Date convertStringAsDate(String data) throws Exception {
		if (data == null || data.equals(""))
			return null;

		java.util.Date date = null;
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			date = new java.sql.Date(((java.util.Date) formatter.parse(data)).getTime());
		} catch (ParseException e) {
			throw e;
		}
		return date;
	}

	/**
	 * Converte um Long para uma String no formato de minutos mm:ss.
	 * 
	 * @param minutos
	 *            Long com os minutos a ser formatado
	 * @return String formatada mm:ss
	 */
	public static String segundoToMinuto(Long segundos) {
		String saida;
		if (segundos > 0) {
			Long divisao = segundos / 60;
			Long resto = segundos % 60;
			saida = divisao + ":";
			if (resto > 0) {
				saida = saida + resto;
			} else {
				saida = saida + "00";
			}
		} else {
			saida = "0:00";
		}
		return saida;
	}

}
