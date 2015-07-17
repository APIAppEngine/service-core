package apiserver.services;

/**
 * Created by mnimer on 10/29/14.
 */
//@Service

public class MashapeUserDetailService //implements AuthenticationUserDetailsService
{
    /**
    @Override public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException
    {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add( new SimpleGrantedAuthority("mashape-request") );
        UserDetails userDetails = new User("mashape", token.toString(), authorities);
        return userDetails;
    }

    **/
}
