package de.jdufner;

/*
create table mg_bestellkopf (
  fnr smallint not null,
  bestelldatum date not null,
  dateinummer smallint,
  bestellwert float,
  lieferdatum varchar(50),
  upprog varchar(50) not null,
  uptimestamp timestamp not null
)

insert into mg_bestellkopf values (1, '2004-06-05', 0, 0, null, 'UNKNOWN', '2004-06-05 00:00:00')
 */
public class BestellKopf implements java.io.Serializable {

  
  // attributeDeclaration
  
    /**
     * 
     */
          private java.util.Calendar bestelldatum;
      
    /**
     * <p>
     *  The file number for this order.
     * </p>
     */
          private int exportnummer;
      
    /**
     * 
     */
          private java.util.Calendar lieferdatum;
      
    /**
     * 
     */
          private double bestellwert;
      
    /**
     * 
     */
          private java.util.Date uptimestamp;
      
    /**
     * 
     */
          private java.lang.String upprog;
        
  
  // associationDeclaration
            
        /**
         * 
         */
        private de.jdufner.Filiale filiale;
                      
        /**
         * 
         */
        private java.util.Set bestellDetail;
            
  
  // attributeMethods
  
    /**
     * @return The attribute bestelldatum
     */
          public java.util.Calendar getBestelldatum() {
          return this.bestelldatum;
    }

    /**
     * @param bestelldatum The attribute bestelldatum
     */
          public void setBestelldatum(java.util.Calendar bestelldatum) {
          this.bestelldatum = bestelldatum;
    }
  
    /**
     * @return The attribute exportnummer
     */
          public int getExportnummer() {
          return this.exportnummer;
    }

    /**
     * @param exportnummer The attribute exportnummer
     */
          public void setExportnummer(int exportnummer) {
          this.exportnummer = exportnummer;
    }
  
    /**
     * @return The attribute lieferdatum
     */
          public java.util.Calendar getLieferdatum() {
          return this.lieferdatum;
    }

    /**
     * @param lieferdatum The attribute lieferdatum
     */
          public void setLieferdatum(java.util.Calendar lieferdatum) {
          this.lieferdatum = lieferdatum;
    }
  
    /**
     * @return The attribute bestellwert
     */
          public double getBestellwert() {
          return this.bestellwert;
    }

    /**
     * @param bestellwert The attribute bestellwert
     */
          public void setBestellwert(double bestellwert) {
          this.bestellwert = bestellwert;
    }
  
    /**
     * @return The attribute uptimestamp
     */
          public java.util.Date getUptimestamp() {
          return this.uptimestamp;
    }

    /**
     * @param uptimestamp The attribute uptimestamp
     */
          public void setUptimestamp(java.util.Date uptimestamp) {
          this.uptimestamp = uptimestamp;
    }
  
    /**
     * @return The attribute upprog
     */
          public java.lang.String getUpprog() {
          return this.upprog;
    }

    /**
     * @param upprog The attribute upprog
     */
          public void setUpprog(java.lang.String upprog) {
          this.upprog = upprog;
    }
    
  
  // associationMethods
            
        /**
         *
         */
        public de.jdufner.Filiale getFiliale() {
          return this.filiale;
        }
    
        /**
         *
         */
        public void setFiliale(de.jdufner.Filiale filiale) {
          this.filiale = filiale;
        }
                      
        /**
         *
         */
        public java.util.Set getBestellDetail() {
          return this.bestellDetail;
        }
    
        /**
         *
         */
        public void setBestellDetail(java.util.Set bestellDetail) {
          this.bestellDetail = bestellDetail;
        }
            
  /**
   * @see java.lang.Object#equals(Object other)
   */
  public boolean equals(Object other) {
    if (this == other) {
    return true;
  }
    if (other == null) {
      return false;
    }
    if (this.getClass() == other.getClass()) {
      BestellKopf that = (BestellKopf) other;
              
          if (this.getBestelldatum() != null && that.getBestelldatum() != null) {
            if (!this.getBestelldatum().equals(that.getBestelldatum())) return false;
          } else {
            if (this.getBestelldatum() != null || that.getBestelldatum() != null) {
              return false;
            }
          }
                              
          if (this.getExportnummer() != that.getExportnummer()) return false;
                      
          if (this.getLieferdatum() != null && that.getLieferdatum() != null) {
            if (!this.getLieferdatum().equals(that.getLieferdatum())) return false;
          } else {
            if (this.getLieferdatum() != null || that.getLieferdatum() != null) {
              return false;
            }
          }
                              
          if (this.getBestellwert() != that.getBestellwert()) return false;
                      
          if (this.getUptimestamp() != null && that.getUptimestamp() != null) {
            if (!this.getUptimestamp().equals(that.getUptimestamp())) return false;
          } else {
            if (this.getUptimestamp() != null || that.getUptimestamp() != null) {
              return false;
            }
          }
                      
          if (this.getUpprog() != null && that.getUpprog() != null) {
            if (!this.getUpprog().equals(that.getUpprog())) return false;
          } else {
            if (this.getUpprog() != null || that.getUpprog() != null) {
              return false;
            }
          }
                                                   // todo
                                                       // todo
                        
      return true;
    }
    return false;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    int hashCode = 0;
      try {          
        if(this.getBestelldatum() != null) {
          hashCode += this.getBestelldatum().hashCode();
        }
                      
        hashCode += new java.lang.Integer(this.getExportnummer()).hashCode();
                      
        if(this.getLieferdatum() != null) {
          hashCode += this.getLieferdatum().hashCode();
        }
                      
        hashCode += new java.lang.Double(this.getBestellwert()).hashCode();
                      
        if(this.getUptimestamp() != null) {
          hashCode += this.getUptimestamp().hashCode();
        }
                      
        if(this.getUpprog() != null) {
          hashCode += this.getUpprog().hashCode();
        }
      } catch (Exception e) {
        System.out.println("EXCEPTION!!!");
      }
      return hashCode;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer sb = new StringBuffer(super.toString());
          sb.append(" bestelldatum=");
      sb.append(getBestelldatum());
          sb.append(" exportnummer=");
      sb.append(getExportnummer());
          sb.append(" lieferdatum=");
      sb.append(getLieferdatum());
          sb.append(" bestellwert=");
      sb.append(getBestellwert());
          sb.append(" uptimestamp=");
      sb.append(getUptimestamp());
          sb.append(" upprog=");
      sb.append(getUpprog());
                                    return sb.toString();
  }
  
}

