package com.googlecode.gflot.examples.client.mvp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;

public abstract class AbstractPlaceHistoryMapper
    implements PlaceHistoryMapper
{
    protected static final String SEPARATOR_TOKEN_PARAMETERS = ":";

    protected static final String SEPARATOR_PARAMETERS = "&";

    protected static final String SEPARATOR_PARAMETER_ID_VALUE = "=";

    @Override
    public Place getPlace( String token )
    {
        if ( null == token || "".equals( token ) )
        {
            // it will show the default place
            return null;
        }

        Place place = getPlaceFromToken( token );

        if ( null == place )
        {
            GWT.log( "No corresponding place found for the token " + token );
            return null;
        }

        // if the place has parameters, retrieving them from token
        if ( place instanceof PlaceWithParameters )
        {
            int index = token.indexOf( SEPARATOR_TOKEN_PARAMETERS );
            if ( index != -1 )
            {
                String[] parameters = token.substring( index + 1 ).split( SEPARATOR_PARAMETERS );
                Map<String, String> mapParameters = new HashMap<String, String>( parameters.length );
                for ( String parameter : parameters )
                {
                    String[] paramIdValue = parameter.split( SEPARATOR_PARAMETER_ID_VALUE );
                    mapParameters.put( paramIdValue[0], paramIdValue[1] );
                }
                ( (PlaceWithParameters) place ).setParameters( mapParameters );
            }
        }

        return place;
    }

    /**
     * Find a place corresponding to the token give in parameters.
     *
     * @param token a String token
     * @return the place corresponding to the token, null if none found
     */
    protected abstract Place getPlaceFromToken( String token );

    @Override
    public String getToken( Place place )
    {
        String token = getTokenFromPlace( place );

        if ( place instanceof PlaceWithParameters )
        {
            Map<String, String> parameters = ( (PlaceWithParameters) place ).getParameters();
            if ( null != parameters && !parameters.isEmpty() )
            {
                StringBuilder tokenBuilder = new StringBuilder( token );
                tokenBuilder.append( SEPARATOR_TOKEN_PARAMETERS );
                boolean first = true;
                for ( Entry<String, String> parameter : parameters.entrySet() )
                {
                    if ( !first )
                    {
                        tokenBuilder.append( SEPARATOR_PARAMETERS );
                    }
                    tokenBuilder.append( parameter.getKey() );
                    tokenBuilder.append( SEPARATOR_PARAMETER_ID_VALUE );
                    tokenBuilder.append( parameter.getValue() );
                    first = false;
                }
                token = tokenBuilder.toString();
            }
        }
        return token;
    }

    protected abstract String getTokenFromPlace( Place place );
}
